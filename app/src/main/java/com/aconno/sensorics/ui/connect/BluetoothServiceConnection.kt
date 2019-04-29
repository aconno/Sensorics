package com.aconno.sensorics.ui.connect

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.StringRes
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class BluetoothServiceConnection : ServiceConnection {

    //public
    var connectionCallbacks: MutableList<ConnectionCallback> = mutableListOf()

    private var serviceConnect: BluetoothConnectService? = null
    private val connectResultsDisposable = CompositeDisposable()

    override fun onServiceDisconnected(name: ComponentName?) {
        connectResultsDisposable.clear()
        serviceConnect = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()
        connectionCallbacks
            .takeIf { it.size > 0 }
            ?.let { connectionCallbacks ->

                val subscribe = serviceConnect?.getConnectResults()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe {
                        when {
                            it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                                connectionCallbacks.onStatusTextChanged(R.string.device_not_found)
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                                connectionCallbacks.onStatusTextChanged(R.string.connecting)
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                                connectionCallbacks.onStatusTextChanged(R.string.connected)
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                                connectionCallbacks.onStatusTextChanged(R.string.discovered)
                                connectionCallbacks.onConnected()
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                                connectionCallbacks.onStatusTextChanged(R.string.disconnected)
                                connectionCallbacks.onDisconnected()
                                //Close Connection
                                serviceConnect?.close()
                            }
                            it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                                connectionCallbacks.onStatusTextChanged(R.string.error)
                            }
                            it.action == BluetoothGattCallback.ACTION_BEACON_HAS_SETTINGS -> {
                                connectionCallbacks.onStatusTextChanged(R.string.discovered)
                                connectionCallbacks.onHasSettings()
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
    //Private Functions

    private fun MutableList<ConnectionCallback>.onStatusTextChanged(@StringRes stringId: Int) {
        forEach {
            it.onStatusTextChanged(stringId)
        }
    }

    private fun MutableList<ConnectionCallback>.onConnected() {
        forEach {
            it.onConnected()
        }
    }

    private fun MutableList<ConnectionCallback>.onDisconnected() {
        forEach {
            it.onDisconnected()
        }
    }

    private fun MutableList<ConnectionCallback>.onHasSettings() {
        forEach {
            it.onHasSettings()
        }
    }

    //Public Functions
    fun registerConnectionCallback(connectionCallback: ConnectionCallback) {
        connectionCallbacks.add(connectionCallback)
    }

    fun unregisterConnectionCallback(connectionCallback: ConnectionCallback? = null) {
        if (connectionCallback == null) {
            connectionCallbacks.clear()
        } else {
            connectionCallbacks.remove(connectionCallback)
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

    class ConnectionCallbackAdapter : ConnectionCallback {
        override fun onStatusTextChanged(stringRes: Int) {
        }

        override fun onHasSettings() {
        }

        override fun onConnected() {
        }

        override fun onDisconnected() {
        }
    }
}