package com.aconno.sensorics.ui.log

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_toolbar.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LogActivity : AppCompatActivity() {
    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var frag: LogFragment
    private lateinit var serviceUUID: UUID
    private lateinit var characteristicUUID: UUID
    private val publishSubject = PublishSubject.create<LogLine>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            bluetoothConnectService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothConnectService =
                (service as? BluetoothConnectService.LocalBinder)?.getService()
            Timber.d("Connected")

            bluetoothConnectService?.let { bluetoothService ->
                bluetoothService.getConnectResultsLiveData().observe(this@LogActivity,
                    Observer<GattCallbackPayload> {
                        onNewPayload(it)
                    })

                bluetoothService.startConnectionStream()

                bluetoothService.connect(macAddress)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)
        toolbar.title = getString(R.string.logging)
        setSupportActionBar(toolbar)

        invalidateOptionsMenu()

        getDeviceMacAddress(savedInstanceState)
        val fm = supportFragmentManager
        frag = LogFragment.newInstance(publishSubject)
        fm.findFragmentById(R.id.content_container) ?:
            fm.beginTransaction().add(
                R.id.content_container,
                frag
            ).commit()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DEVICE_MAC_ADDRESS, macAddress)
    }

    private fun onNewPayload(payload: GattCallbackPayload) {
        Timber.i(payload.action)
        when (payload.action) {
            BluetoothGattCallback.ACTION_BEACON_HAS_LOGS -> {
                Timber.i("Logs discovered")
                onBeaconHasLogs(payload.payload)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                Timber.i("Device disconnected")
                bluetoothConnectService?.close()
            }
            BluetoothGattCallback.ACTION_DATA_AVAILABLE -> {
                onCharacteristicChanged(payload.payload)
            }
            BluetoothGattCallback.ACTION_GATT_DESCRIPTOR_WRITE -> {
                onDescriptorWritten(payload.payload)
            }
            else -> {
                return
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.toggle_auto_scroll -> frag.toggleAutoScroll()
            R.id.export_logs -> frag.importFromWebView()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val mainMenu = menu
        mainMenu.clear()
        menuInflater.inflate(R.menu.menu_logs, menu)

        return super.onPrepareOptionsMenu(menu)
    }

    private fun onCharacteristicChanged(payload: Any?) {
        val characteristic = payload as? BluetoothGattCharacteristic
        characteristic?.let {
            publishSubject.onNext(LogLine(it.getStringValue(0)))
        }
    }

    private fun getDeviceMacAddress(savedInstanceState: Bundle?) {
        intent?.takeIf { it.hasExtra(DEVICE_MAC_ADDRESS) }?.let {
            macAddress = it.getStringExtra(
                DEVICE_MAC_ADDRESS
            )
        }

        savedInstanceState?.takeIf { it.containsKey(DEVICE_MAC_ADDRESS) }?.let {
            macAddress = it.getString(DEVICE_MAC_ADDRESS, "")
        }

        if (!::macAddress.isInitialized || macAddress.isBlank()) {
            Timber.e("Invalid MAC Address")
            finish()
        }
    }

    private fun onBeaconHasLogs(payload: Any?) {
        val characteristic = payload as? BluetoothGattCharacteristic

        characteristic?.let {
            characteristicUUID = it.uuid
            serviceUUID = it.service.uuid
            Timber.d("Enabling characteristic for: " + characteristicUUID)
            bluetoothConnectService?.enableNotifications(
                characteristicUUID = characteristicUUID,
                serviceUUID = serviceUUID,
                isEnabled = true
            )
        }
    }

    private fun onDescriptorWritten(payload: Any?) {
        (payload as? Int)?.let {
            if (it == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("Descriptor operation success")
            } else {
                Timber.e("Descriptor write operation failed")
            }
        }

    }

    companion object {
        private const val DEVICE_MAC_ADDRESS = "device_mac_address"
        fun start(context: Context, deviceMacAddress: String) {
            Intent(context, LogActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }

    /**
     * @param characteristicValue is expected to be formatted like this: "<log_level> tag: message"
     * the colon after tag is optional and length of log_level, tag and message is arbitrary
     */
    class LogLine(characteristicValue : String) {

        val timestamp = SimpleDateFormat("HH:mm:ss:SSS")
            .format(java.util.Calendar.getInstance().time)
        val level : Char
        val tag : String
        val message : String

        init {
            val split = characteristicValue.split("\\s+".toRegex())
            level = split[0].removeSurrounding("<", ">").elementAt(0)
            tag = characteristicValue.split(' ')[1].removeSuffix(":")
            message = split.drop(2).joinToString(" ")
        }

        fun toJson(): String {
            return gson.toJson(this)
        }

        companion object {
            private val gson = Gson()
            fun fromJson(json: String): LogLine {
                return gson.fromJson(json, LogLine::class.java)
            }
        }
    }
}