package com.aconno.sensorics.device.bluetooth

import android.bluetooth.*
import android.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import io.reactivex.subjects.PublishSubject
import java.util.*


class BluetoothGattCallback(
    private val connectGattResults: PublishSubject<GattCallbackPayload>
) : BluetoothGattCallback() {

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_CHAR_WRITE, gatt?.services)
        }
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

            gatt?.getService(UUID.fromString(SETTINGS_SERVICE_UUID))?.let {
                broadcastUpdate(ACTION_BEACON_HAS_SETTINGS)
            }

            findCacheCharacteristic(gatt)?.let {
                broadcastUpdate(ACTION_BEACON_HAS_CACHE, it)
            }
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

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
        broadcastUpdate(ACTION_GATT_DESCRIPTOR_WRITE, status)
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

    private fun broadcastUpdate(update: String, payload: Any?) {
        payload?.let {
            connectGattResults.onNext(
                GattCallbackPayload(update, it)
            )
        }
    }

    private fun findCacheCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return gatt?.services?.find { it.uuid.toString() == CACHE_SERVICE_UUID }
            ?.characteristics?.find { it.uuid.toString() == CACHE_CHARACTERISTIC_UUID }
    }

    companion object {
        const val ACTION_GATT_ERROR = "com.aconno.sensorics.ACTION_GATT_ERROR"
        const val ACTION_GATT_CONNECTING = "com.aconno.sensorics.ACTION_GATT_CONNECTING"
        const val ACTION_GATT_DEVICE_NOT_FOUND = "com.aconno.sensorics.ACTION_GATT_DEVICE_NOT_FOUND"
        const val ACTION_GATT_CONNECTED = "com.aconno.sensorics.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.aconno.sensorics.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.aconno.sensorics.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.aconno.sensorics.ACTION_DATA_AVAILABLE"
        const val ACTION_GATT_CHAR_WRITE = "com.aconno.sensorics.ACTION_GATT_CHAR_WRITE"
        const val ACTION_BEACON_HAS_SETTINGS = "com.aconno.sensorics.ACTION_BEACON_HAS_SETTINGS"
        const val ACTION_BEACON_HAS_CACHE = "com.aconno.sensorics.ACTION_BEACON_HAS_CACHE"
        const val ACTION_GATT_DESCRIPTOR_WRITE = "com.aconno.sensorics.ACTION_GATT_DESCRIPTOR_WRITE"
        private const val SETTINGS_SERVICE_UUID = "cc52c000-9adb-4c37-bc48-376f5fee8851"
        private const val CACHE_SERVICE_UUID = "cc521000-9adb-4c37-bc48-376f5fee8851"
        private const val CACHE_CHARACTERISTIC_UUID = "cc521001-9adb-4c37-bc48-376f5fee8851"
    }
}