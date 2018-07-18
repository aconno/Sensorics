package com.aconno.sensorics.device.bluetooth

import android.bluetooth.*
import android.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import io.reactivex.subjects.PublishSubject


class BluetoothGattCallback(
    private val connectGattResults: PublishSubject<GattCallbackPayload>
) : BluetoothGattCallback() {

    companion object {
        val ACTION_GATT_ERROR = "com.aconno.sensorics.ACTION_GATT_ERROR"
        val ACTION_GATT_CONNECTING = "com.aconno.sensorics.ACTION_GATT_CONNECTING"
        val ACTION_GATT_DEVICE_NOT_FOUND = "com.aconno.sensorics.ACTION_GATT_DEVICE_NOT_FOUND"
        val ACTION_GATT_CONNECTED = "com.aconno.sensorics.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.aconno.sensorics.ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "com.aconno.sensorics.ACTION_GATT_SERVICES_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "com.aconno.sensorics.ACTION_DATA_AVAILABLE"
    }


    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            broadcastUpdate(ACTION_GATT_CONNECTED)
            gatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            broadcastUpdate(ACTION_GATT_DISCONNECTED)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt?.services)
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
    }

    fun updateDeviceNotFound() {
        broadcastUpdate(ACTION_GATT_DEVICE_NOT_FOUND)
    }

    fun updateConnecting() {
        broadcastUpdate(ACTION_GATT_CONNECTING)
    }

    fun updateError() {
        broadcastUpdate(ACTION_GATT_ERROR)
    }

    private fun broadcastUpdate(
        update: String
    ) {
        connectGattResults.onNext(
            GattCallbackPayload(
                update
            )
        )
    }

    private fun broadcastUpdate(
        update: String,
        characteristic: BluetoothGattCharacteristic?
    ) {
        characteristic?.let {
            val data = readCharacteristic(it)

            connectGattResults.onNext(
                GattCallbackPayload(
                    update,
                    data
                )
            )
        }
    }

    private fun broadcastUpdate(
        update: String,
        services: List<BluetoothGattService>?
    ) {
        services?.let {
            connectGattResults.onNext(
                GattCallbackPayload(
                    update,
                    services
                )
            )
        }
    }

    private fun readCharacteristic(characteristic: BluetoothGattCharacteristic): String {
        return "READ"
    }
}