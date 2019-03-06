package com.aconno.sensorics.ui.logs

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.aconno.bluetooth.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.model.LogModel
import com.aconno.sensorics.viewmodel.LoggingViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_logging.*
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

class LoggingActivity : DaggerAppCompatActivity(), BluetoothImpl.BluetoothEnableRequestListener {
    private lateinit var logAdapter: LoggingAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var device: Device
    private var bluetoothDevice: BluetoothDevice? = null
    private var scanDisposable: Disposable? = null
    private var scrollToBottom: Boolean = true
    private var lastVisiblePosition = -1
    private lateinit var bluetoothGattCallback: BluetoothGattCallback

    @Inject
    lateinit var loggingViewModel: LoggingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)
        if (!intent.hasExtra(EXTRA_DEVICE)) {
            throw IllegalArgumentException("This activity must have $EXTRA_DEVICE as an extra")
        } else {
            device = Gson().fromJson(intent.getStringExtra(EXTRA_DEVICE), Device::class.java)
        }

        if (savedInstanceState == null) {
            initViews()
        }

        loggingViewModel.getLogItemsLiveData()
                .observe(this, android.arch.lifecycle.Observer { handleLogEvent(it) })

        startScan()
    }

    override fun onResume() {
        super.onResume()
        fetchLogs()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothDevice?.disconnect()
        scanDisposable?.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBluetoothRequestActivityResult() {
    }

    private fun initViews() {
        initActionBar()

        initAdapter()

        initScrollListener()

        initRecyclerView()

        initButtons()
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.log_activity_title_format, device.getRealName())
            subtitle = device.macAddress
        }
    }

    private fun initAdapter() {
        logAdapter = LoggingAdapter()
        logAdapter.setOnSelectionChangedListener(object : LoggingAdapter.OnSelectionChangedListener {
            override fun onSelectionChanged() {
                btnScrollToBottom.isChecked = false
            }
        })
    }

    private fun initScrollListener() {
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
    }

    private fun initRecyclerView() {
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
            }
        })
    }

    private fun initButtons() {
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

    private fun startScan() {
        BluetoothImpl(this).let { bluetooth ->

            initGattCallback(bluetooth)
            //TODO Uncomment this line and remove line 152 when fw is updated
            //scanDisposable = bluetooth.startScanForDevice(device.macAddress, Consumer { sr ->
            scanDisposable = bluetooth.startScanForDevice("FE:95:3C:B4:D2:92", Consumer { scanResult ->
                if (bluetoothDevice != null) {
                    return@Consumer
                }

                bluetoothDevice = BluetoothDeviceImpl(this, scanResult.device)
                bluetooth.connect(bluetoothDevice!!, bluetoothGattCallback)
                bluetooth.stopScan()
            })
            logWarning(getString(R.string.log_started_scanning))
        }
    }

    private fun initGattCallback(bluetooth: Bluetooth) {
        bluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onDeviceConnected(device: BluetoothDevice) {
                super.onDeviceConnected(device)
                this@LoggingActivity.onDeviceConnected(bluetooth)
            }

            override fun onDeviceConnecting(device: BluetoothDevice) {
                super.onDeviceConnecting(device)
                logWarning(getString(R.string.connecting))
            }

            override fun onDeviceDisconnected(device: BluetoothDevice) {
                super.onDeviceDisconnected(device)
                this@LoggingActivity.onDeviceDisconnected(device)
            }

            override fun onServicesDiscovered(device: BluetoothDevice) {
                super.onServicesDiscovered(device)
                this@LoggingActivity.onServicesDiscovered(device)
            }
        }
    }

    private fun onDeviceConnected(bluetooth: Bluetooth) {
        logInfo(getString(R.string.connected))
        bluetooth.stopScan()
    }

    private fun onDeviceDisconnected(bluetoothDevice: BluetoothDevice) {
        this@LoggingActivity.bluetoothDevice = null
        bluetoothDevice.removeBluetoothGattCallback(bluetoothGattCallback)
        logError(getString(R.string.disconnected))
        showDisconnectionAlertDialog()
    }

    private fun onServicesDiscovered(bluetoothDevice: BluetoothDevice) {
        logInfo(getString(R.string.log_services_discovered, bluetoothDevice.services.size))
        logDiscoveredServices(bluetoothDevice.services)

        val uuid = UUID.fromString(LOG_UUID)
        bluetoothDevice.setCharacteristicNotification(uuid, true)
        bluetoothDevice.addCharacteristicChangedListener(uuid, object : CharacteristicChangedListener {
            override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
                runOnUiThread {
                    logInfo(value.toString(Charset.defaultCharset()))
                }
            }
        })
    }

    private fun refreshLogs(logs: List<LogModel>) {
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
        Snackbar.make(layoutRoot, R.string.log_cleared_logs, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.log_undo_clear) {
                logAdapter.undoClear()
                if (lastVisiblePosition != -1) {
                    rvLogs.scrollToPosition(lastVisiblePosition)
                }
                dismiss()
            }
            addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        clearDeviceLogs()
                    }
                }
            })
            setActionTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }.show()
    }

    private fun logDiscoveredServices(services: List<BluetoothGattService>) {
        services.forEach { service ->
            (service as BluetoothGattService?)?.let {
                loggingViewModel.logInfo(getString(R.string.log_service_discovered_format, it.uuid),
                        device.macAddress)

                it.characteristics.forEach { characteristic ->
                    logInfo(getString(R.string.log_characteristic_format, characteristic.uuid,
                            characteristic.value))
                }
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
                    .setTitle(R.string.log_disconnected_dialog_title)
                    .setMessage(getString(R.string.log_disconnected_dialog_message_format, device.name, device.macAddress))
                    .setPositiveButton(R.string.log_back_to_scanner) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .setNegativeButton(R.string.log_review_logs) { dialog, _ -> dialog.dismiss() }
                    .create().also {
                        if (!isFinishing or !isDestroyed) {
                            it.show()
                        }
                    }
        }
    }

    companion object {
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val LOG_UUID = "00002a19-0000-1000-8000-00805f9b34fb"
        fun start(context: Context, device: Device) {
            Intent(context, LoggingActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, Gson().toJson(device))
            }.also {
                context.startActivity(it)
            }
        }
    }
}