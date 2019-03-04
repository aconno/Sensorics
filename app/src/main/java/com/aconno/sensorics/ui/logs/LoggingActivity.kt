package com.aconno.sensorics.ui.logs

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.aconno.bluetooth.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_logging.*
import timber.log.Timber
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class LoggingActivity : DaggerAppCompatActivity(), BluetoothImpl.BluetoothEnableRequestListener {
    private lateinit var device: Device
    private lateinit var logAdapter: LoggingAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var bluetoothDevice: BluetoothDevice? = null
    private var scrollToBottom: Boolean = true
    private var lastVisiblePosition = -1

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
        startScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothDevice?.disconnect()
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

    private fun startScan() {
        BluetoothImpl(this).let { bluetooth ->
            bluetooth.startScanForDevice(device.macAddress, Consumer { sr ->
                if (bluetoothDevice != null) return@Consumer
                bluetoothDevice = BluetoothDeviceImpl(this, sr.device)
                bluetooth.connect(bluetoothDevice!!, object : BluetoothGattCallback() {
                    override fun onDeviceConnected(device: BluetoothDevice) {
                        log("Connected")
                        bluetooth.stopScan()
                    }

                    override fun onDeviceConnecting(device: BluetoothDevice) {
                        logWarning("Connecting...")
                    }

                    override fun onDeviceDisconnected(device: BluetoothDevice) {
                        this@LoggingActivity.bluetoothDevice = null
                        device.removeBluetoothGattCallback(this)
                        logError("Disconnected")
                        showDisconnectionAlertDialog()
                    }

                    override fun onServicesDiscovered(device: BluetoothDevice) {
                        super.onServicesDiscovered(device)

                        log("${device.services.size} services discovered")
                        device.services.flatMap { it.characteristics }.forEach {
                            log("Characteristic with UUID: ${it.uuid} with value: ${it.value
                                    ?: "null"} from service: ${it.service.uuid}")
                        }

                        val uuid = UUID.fromString(LOG_UUID)
                        device.setCharacteristicNotification(uuid, true)
                        device.addCharacteristicChangedListener(uuid, object : CharacteristicChangedListener {
                            override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
                                runOnUiThread {
                                    log(value.toString(Charset.defaultCharset()))
                                }
                            }
                        })
                    }
                })
                bluetooth.stopScan()
            })
            logWarning("Started scanning...")
        }
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
        runOnUiThread {
            logAdapter.addLog(log, loggingLevel)
            if (scrollToBottom) {
                rvLogs.smoothScrollToPosition(logAdapter.itemCount - 1)
            }
        }
    }

    private fun getFormattedLog(info: String): String {
        val dateFormat = SimpleDateFormat(LOG_DATE_FORMAT, Locale.getDefault())
        val formattedTime = dateFormat.format(Date())
        return String.format(LOG_FORMAT, formattedTime, info)
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
            setActionTextColor(ContextCompat.getColor(applicationContext, R.color.primaryColor))
        }.show()
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

    override fun onBluetoothRequestActivityResult() {
        //no-op
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