package com.aconno.sensorics.ui.logs

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.aconno.bluetooth.BluetoothDeviceService
import com.aconno.bluetooth.CharacteristicChangedListener
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.ui.configure.BeaconViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_logging.*
import timber.log.Timber
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LoggingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var beaconViewModel: BeaconViewModel
    private lateinit var device: Device
    private lateinit var logAdapter: LoggingAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var serviceConnect: BluetoothDeviceService? = null
    private var connectResultDisposable: Disposable? = null
    private var scrollToBottom: Boolean = true

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            beaconViewModel.beacon.removeObserver(beaconObserver)
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothDeviceService.LocalBinder).getService()
            Timber.d("Connected")
            openConnection()
        }
    }

    private val beaconObserver: Observer<Beacon> = Observer {
        val device = it?.device
        device?.apply {
            val uuid = UUID.fromString(LOG_UUID)
            setCharacteristicNotification(uuid, true)
            addCharacteristicChangedListener(uuid, object : CharacteristicChangedListener {
                override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
                    runOnUiThread {
                        log(value.toString(Charset.defaultCharset()))
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)
        if (intent.extras != null && intent.extras!!.containsKey(LoggingActivity.EXTRA_DEVICE)) {
            device = Gson().fromJson(
                    intent.extras!!.getString(LoggingActivity.EXTRA_DEVICE)
                    , Device::class.java
            )
        } else {
            throw IllegalArgumentException("Device not provided.")
        }

        if (savedInstanceState == null) {
            initViews()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BluetoothDeviceService::class.java).also {
            bindService(
                    it, serviceConnection, Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        unbindService(serviceConnection)
        closeConnection()
        super.onStop()
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

        btnClearAll.setOnClickListener { clear() }
        btnScrollToBottom.setOnCheckedChangeListener { _, isChecked ->
            scrollToBottom = isChecked
            if (isChecked) {
                rvLogs.addOnScrollListener(scrollListener)
                rvLogs.smoothScrollToPosition(logAdapter.itemCount - 1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openConnection() {
        log(getString(R.string.connecting))
        serviceConnect?.connectToBluetoothDevice(object :
                BluetoothDeviceService.LoadingTasksUIInterface {

            override fun onDisconnected() {
                runOnUiThread {
                    showSnackBar("Disconnected")
                    logError("Disconnected")
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int) {
                runOnUiThread {
                    val info = "Connection Progress Tasks Completed: $tasksCompleted/$tasksTotal"
                    log(info)
                }
            }

            override fun onTasksComplete(beacon: Beacon) {
                runOnUiThread {
                    log("All connection tasks completed")
                    beaconViewModel.beacon.value = beacon
                    beaconViewModel.beacon.observe(this@LoggingActivity,
                            beaconObserver)
                }
            }

            override fun onTasksCancelled() {
                logWarning("Terminating connection, going back to the scanner!")
            }
        }, device.macAddress)
    }

    @UiThread
    private fun log(info: String) {
        log(info, LoggingAdapter.LoggingLevel.INFO)
    }

    @UiThread
    private fun logError(info: String) {
        log(info, LoggingAdapter.LoggingLevel.ERROR)
    }

    @UiThread
    private fun logWarning(info: String) {
        log(info, LoggingAdapter.LoggingLevel.WARNING)
    }

    @UiThread
    private fun log(info: String, loggingLevel: LoggingAdapter.LoggingLevel) {
        val log = getFormattedLog(info)
        logAdapter.addLog(log, loggingLevel)
        if (scrollToBottom) {
            rvLogs.smoothScrollToPosition(logAdapter.itemCount - 1)
        }
    }

    private fun getFormattedLog(info: String): String {
        val dateFormat = SimpleDateFormat(LOG_DATE_FORMAT, Locale.getDefault())
        val formattedTime = dateFormat.format(Date())
        return String.format(LOG_FORMAT, formattedTime, info)
    }

    @UiThread
    private fun clear() {
        val scrollX = rvLogs.scrollX
        val scrollY = rvLogs.scrollY
        logAdapter.clear()
        Snackbar.make(layoutRoot, "Cleared log list", Snackbar.LENGTH_LONG).also { snack ->
            snack.setAction("Undo") {
                logAdapter.undoClear()
                rvLogs.scrollTo(scrollX, scrollY)
                snack.dismiss()
            }
            snack.setActionTextColor(ContextCompat.getColor(applicationContext, R.color.primaryColor))
        }.show()
    }

    @UiThread
    private fun showSnackBar(message: String) {
        Snackbar.make(layoutRoot, message, Snackbar.LENGTH_LONG).show()
    }

    private fun closeConnection() {
        Timber.d("Close Connection")
        serviceConnect?.disconnect()
    }

    companion object {
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val LOG_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SS"
        private const val LOG_FORMAT = "%s : %s"
        private const val LOG_UUID = "cc52a001-9adb-4c37-bc48-376f5fee8851"
        fun start(context: Context, device: Device) {
            Intent(context, LoggingActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))
            }.also {
                context.startActivity(it)
            }
        }
    }
}