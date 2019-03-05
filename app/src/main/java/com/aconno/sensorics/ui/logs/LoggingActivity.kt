package com.aconno.sensorics.ui.logs

import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.UiThread
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.LogModel
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.viewmodel.LoggingViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_logging.*
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

class LoggingActivity : DaggerAppCompatActivity() {
    private lateinit var logAdapter: LoggingAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var device: Device
    private var scrollToBottom: Boolean = true
    private var lastVisiblePosition = -1
    private var menu: Menu? = null
    private var serviceConnect: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null

    @Inject
    lateinit var loggingViewModel: LoggingViewModel

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()
            Timber.d("Connected")

            connectResultDisposable = serviceConnect!!.getConnectResults()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Timber.d(it.action)
                        when {
                            it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                                logError(getString(R.string.device_not_found))
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                                logWarning(getString(R.string.connecting))
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                                logInfo(getString(R.string.connected))
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                                serviceConnect?.enableLogging()
                                logInfo(getString(R.string.connected))
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                                if (menu != null) {
                                    val item: MenuItem = menu!!.findItem(R.id.action_toggle_connect)
                                    item.title = getString(R.string.connect)
                                }
                                logError(getString(R.string.disconnected))
                                showDisconnectionAlertDialog()
                                serviceConnect?.close()
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                                Timber.i("Device Error")
                                logError(getString(R.string.error))
                            }
                            it.action == BluetoothGattCallback.ACTION_DATA_AVAILABLE -> {
                                evaluateLog(it)
                            }
                            else -> {
                                return@subscribe
                            }
                        }
                    }

            serviceConnect?.connect(device.macAddress)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)
        if(!intent.hasExtra(EXTRA_DEVICE)) {
            throw IllegalArgumentException("This activity must have $EXTRA_DEVICE as an extra")
        } else {
            device = Gson().fromJson(intent.getStringExtra(EXTRA_DEVICE), Device::class.java)
        }

        if(savedInstanceState == null) {
            initViews()
        }

        loggingViewModel.getLogItemsLiveData()
                .observe(this, android.arch.lifecycle.Observer { handleLogEvent(it) })
    }

    override fun onResume() {
        super.onResume()
        fetchLogs()
    }

    override fun onStart() {
        super.onStart()
        Intent(applicationContext, BluetoothConnectService::class.java).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = device.getRealName()
            subtitle = device.macAddress
        }

        logAdapter = LoggingAdapter()
        logAdapter.setOnSelectionChangedListener(object : LoggingAdapter.OnSelectionChangedListener {
            override fun onSelectionChanged() {
                btnScrollToBottom.isChecked = false
            }
        })

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lastVisiblePosition = (recyclerView.layoutManager as
                        LinearLayoutManager).findLastVisibleItemPosition()

                rvLogs.adapter?.let {
                    if (scrollToBottom && lastVisiblePosition != it.itemCount - 1 &&
                            newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        btnScrollToBottom.isChecked = false
                        rvLogs.removeOnScrollListener(this)
                    }
                }
            }
        }

        rvLogs.layoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL, false)
        rvLogs.setHasFixedSize(true)
        rvLogs.adapter = logAdapter

        rvLogs.addOnScrollListener(scrollListener)
        rvLogs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as LinearLayoutManager)
                        .findLastVisibleItemPosition().also {
                            if (it != -1) lastVisiblePosition = it
                        }

                Timber.d("Last visible position: $lastVisiblePosition")
            }
        })

        btnClearAll.setOnClickListener { clear() }
        btnScrollToBottom.setOnCheckedChangeListener { _, isChecked ->
            scrollToBottom = isChecked
            if (isChecked) {
                rvLogs.addOnScrollListener(scrollListener)
                scrollToBottom()
            }
        }
    }

    private fun handleLogEvent(logs: ArrayList<LogModel>?) {
                refreshLogs(logs ?: arrayListOf())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun refreshLogs(logs: List<LogModel>) {
        logAdapter.refreshLogs(logs)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        if (scrollToBottom && logAdapter.itemCount > 0) {
            rvLogs.smoothScrollToPosition(logAdapter.itemCount - 1)
        }
    }

    @UiThread
    private fun clear() {
        logAdapter.clear()
        Snackbar.make(layoutRoot, "Cleared log list", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                logAdapter.undoClear()
                if (lastVisiblePosition != -1) {
                    rvLogs.scrollToPosition(lastVisiblePosition)
                }
                dismiss()
            }
            addCallback(object: Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        clearDeviceLogs()
                    }
                }
            })
            setActionTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }.show()
    }

    private fun evaluateLog(gattCallbackPayload: GattCallbackPayload) {
        val characteristic = gattCallbackPayload.payload as BluetoothGattCharacteristic?
        characteristic?.let {
            if(it.uuid.compareTo(UUID.fromString(MainActivity.LOG_UUID)) == 0) {
                logInfo(it.value.toString(Charset.defaultCharset()))
            }
        }
    }

    private fun logInfo(info: String) {
        loggingViewModel.logInfo(info, device.macAddress)
    }

    private fun logWarning(info: String) {
        loggingViewModel.logWarning(info, device.macAddress)
    }

    private fun logError(info: String) {
        loggingViewModel.logError(info, device.macAddress)
    }

    private fun fetchLogs() {
        loggingViewModel.getDeviceLogs(device.macAddress)
    }

    private fun clearDeviceLogs() {
        loggingViewModel.deleteDeviceLogs(device.macAddress)
    }

    private fun showDisconnectionAlertDialog() {
        runOnUiThread {
            AlertDialog.Builder(this)
                    .setOnCancelListener { finish() }
                    .setTitle("Disconnected!")
                    .setMessage("The device: ${device.name} has disconnected, returning to scanner")
                    .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                    .create().also {
                        if (!isFinishing or !isDestroyed) {
                            it.show()
                        }
                    }
        }
    }

    companion object {
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        fun start(context: Context, device: Device) {
            Intent(context, LoggingActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))
            }.also {
                context.startActivity(it)
            }
        }
    }
}