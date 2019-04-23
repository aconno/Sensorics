package com.aconno.sensorics.ui.connect

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.StringRes
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class BluetoothServiceConnection : ServiceConnection {

    //public
    var connectionCallback: ConnectionCallback? = null

    private var serviceConnect: BluetoothConnectService? = null
    private val connectResultsDisposable = CompositeDisposable()

    override fun onServiceDisconnected(name: ComponentName?) {
        connectResultsDisposable.clear()
        serviceConnect = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()
        connectionCallback?.let { connectionCallback ->

            val subscribe = serviceConnect?.getConnectResults()
                ?.subscribe {
                    when {
                        it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                            connectionCallback.onStatusTextChanged(R.string.device_not_found)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                            connectionCallback.onStatusTextChanged(R.string.connecting)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                            connectionCallback.onStatusTextChanged(R.string.connected)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                            connectionCallback.onStatusTextChanged(R.string.discovered)
                            connectionCallback.onConnected()
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                            connectionCallback.onStatusTextChanged(R.string.disconnected)
                            connectionCallback.onDisconnected()
                            //Close Connection
                            serviceConnect?.close()
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                            connectionCallback.onStatusTextChanged(R.string.error)
                        }
                        it.action == BluetoothGattCallback.ACTION_BEACON_HAS_SETTINGS -> {
                            connectionCallback.onStatusTextChanged(R.string.discovered)
                            connectionCallback.onHasSettings()
                        }
                        else -> {
                            return@subscribe
                        }
                    }
                }

            subscribe?.let {
                connectResultsDisposable.add(it)
            }
        }
    }

    fun connect(macAddress: String) {
        serviceConnect?.connect(macAddress)
    }

    fun disconnect() {
        serviceConnect?.disconnect()
    }

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ) {
        serviceConnect?.writeCharacteristic(serviceUUID, characteristicUUID, type, value)
    }

    interface ConnectionCallback {
        fun onStatusTextChanged(@StringRes stringRes: Int)
        fun onHasSettings()
        fun onConnected()
        fun onDisconnected()
    }
}