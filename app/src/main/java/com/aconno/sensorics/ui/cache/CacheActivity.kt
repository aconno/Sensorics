package com.aconno.sensorics.ui.cache

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import com.aconno.sensorics.print
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class CacheActivity : AppCompatActivity() {

    private var bluetoothConnectService: BluetoothConnectService? = null
    private var connectResultDisposable: Disposable? = null
    private lateinit var macAddress: String
    private lateinit var serviceUUID: UUID
    private lateinit var characteristicUUID: UUID

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
                bluetoothService.getConnectResultsLiveData().observe(this@CacheActivity,
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
        getDeviceMacAddress(savedInstanceState)

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
            BluetoothGattCallback.ACTION_BEACON_HAS_CACHE -> {
                Timber.i("Services discovered")
                onBeaconHasCache(payload.payload)
            }
            BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                Timber.i("Device disconnected")
                bluetoothConnectService?.close()
                finish()
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

    private fun onCharacteristicChanged(payload: Any?) {
        val characteristic = payload as? BluetoothGattCharacteristic
        characteristic?.let {
            Timber.d(it.value.print())
        }
    }

    private fun getDeviceMacAddress(savedInstanceState: Bundle?) {
        intent?.takeIf { it.hasExtra(DEVICE_MAC_ADDRESS) }?.let {
            macAddress = it.getStringExtra(
                DEVICE_MAC_ADDRESS
            ) ?: ""
        }

        savedInstanceState?.takeIf { it.containsKey(DEVICE_MAC_ADDRESS) }?.let {
            macAddress = it.getString(DEVICE_MAC_ADDRESS, "")
        }

        if (!::macAddress.isInitialized || macAddress.isBlank()) {
            Timber.e("Invalid MAC Address")
            finish()
        }
    }

    private fun onBeaconHasCache(payload: Any?) {
        val characteristic = payload as? BluetoothGattCharacteristic
        characteristic?.let {
            characteristicUUID = it.uuid
            serviceUUID = it.service.uuid

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
            Intent(context, CacheActivity::class.java).also {
                it.putExtra(DEVICE_MAC_ADDRESS, deviceMacAddress)
                context.startActivity(it)
            }
        }
    }
}