package com.aconno.sensorics.device.bluetooth

import android.bluetooth.*
import android.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.model.GattCallbackPayload
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*


class BluetoothGattCallback(
    private val connectGattResults: PublishSubject<GattCallbackPayload>
) : BluetoothGattCallback() {

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            broadcastUpdate(ACTION_GATT_CONNECTED)
            gatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            broadcastUpdate(ACTION_GATT_DISCONNECTED, status)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt?.services)

            val parameterSaveCharacteristic = findParameterSaveCharacteristic(gatt)?.also {
                broadcastUpdate(ACTION_BEACON_HAS_PARAMETER_SAVE, it)
            }
            val parameterBulkCharacteristic = findParameterBulkCharacteristic(gatt)?.also {
                broadcastUpdate(ACTION_BEACON_HAS_PARAMETER_BULK, it)
            }
            val slotSaveCharacteristic = findSlotSaveCharacteristic(gatt)?.also {
                broadcastUpdate(ACTION_BEACON_HAS_SLOT_SAVE, it)
            }
            val slotBulkCharacteristic = findSlotBulkCharacteristic(gatt)?.also {
                broadcastUpdate(ACTION_BEACON_HAS_SLOT_BULK, it)
            }

            val settingsCharacteristics = listOf(
                parameterSaveCharacteristic, parameterBulkCharacteristic,
                slotSaveCharacteristic, slotBulkCharacteristic
            )

            if (settingsCharacteristics.all { it != null }) {
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
        } else {
            broadcastUpdate(ACTION_DATA_AVAILABLE_FAIL, characteristic)
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_CHAR_WRITE, gatt?.services)
        } else {
            broadcastUpdate(ACTION_GATT_CHAR_WRITE_FAIL, gatt?.services)
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorRead(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_DESCRIPTOR_READ, descriptor)
        } else {
            broadcastUpdate(ACTION_GATT_DESCRIPTOR_READ_FAIL, status)
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_DESCRIPTOR_WRITE, descriptor)
        } else {
            broadcastUpdate(ACTION_GATT_DESCRIPTOR_WRITE_FAIL, status)
        }
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_MTU_CHANGED, mtu)
        } else {
            Timber.e("Couldn't change MTU!")
        }
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

    private fun findCharacteristic(
        gatt: BluetoothGatt?,
        serviceUUID: UUID,
        characteristicUUID: UUID
    ): BluetoothGattCharacteristic? {
        return gatt?.services?.find { it.uuid.toString() == serviceUUID.toString() }
            ?.characteristics?.find { it.uuid.toString() == characteristicUUID.toString() }
    }

    private fun findCacheCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return findCharacteristic(gatt, CACHE_SERVICE_UUID, CACHE_CHARACTERISTIC_UUID)
    }

    private fun findSlotSaveCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return findCharacteristic(gatt, SLOT_SERVICE_UUID, SLOT_SAVE_CHARACTERISTIC_UUID)
    }

    private fun findSlotBulkCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return findCharacteristic(gatt, SLOT_SERVICE_UUID, SLOT_BULK_CHARACTERISTIC_UUID)
    }

    private fun findParameterSaveCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return findCharacteristic(gatt, PARAMETER_SERVICE_UUID, PARAMETER_SAVE_CHARACTERISTIC_UUID)
    }

    private fun findParameterBulkCharacteristic(gatt: BluetoothGatt?): BluetoothGattCharacteristic? {
        return findCharacteristic(gatt, PARAMETER_SERVICE_UUID, PARAMETER_SAVE_CHARACTERISTIC_UUID)
    }

    companion object {
        const val ACTION_GATT_ERROR = "com.aconno.sensorics.ACTION_GATT_ERROR"
        const val ACTION_GATT_CONNECTING = "com.aconno.sensorics.ACTION_GATT_CONNECTING"
        const val ACTION_GATT_DEVICE_NOT_FOUND = "com.aconno.sensorics.ACTION_GATT_DEVICE_NOT_FOUND"
        const val ACTION_GATT_CONNECTED = "com.aconno.sensorics.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.aconno.sensorics.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "com.aconno.sensorics.ACTION_GATT_SERVICES_DISCOVERED"

        const val ACTION_BEACON_HAS_SETTINGS = "com.aconno.sensorics.ACTION_BEACON_HAS_SETTINGS"
        const val ACTION_BEACON_HAS_CACHE = "com.aconno.sensorics.ACTION_BEACON_HAS_CACHE"

        const val ACTION_BEACON_HAS_PARAMETER_SAVE = "com.aconno.sensorics.ACTION_BEACON_HAS_PARAMETER_SAVE"
        const val ACTION_BEACON_HAS_PARAMETER_BULK = "com.aconno.sensorics.ACTION_BEACON_HAS_PARAMETER_BULK"
        const val ACTION_BEACON_HAS_SLOT_SAVE = "com.aconno.sensorics.ACTION_BEACON_HAS_SLOT_SAVE"
        const val ACTION_BEACON_HAS_SLOT_BULK = "com.aconno.sensorics.ACTION_BEACON_HAS_SLOT_BULK"

        const val ACTION_GATT_MTU_CHANGED = "com.aconno.sensorics.ACTION_GATT_MTU_CHANGED"

        const val ACTION_DATA_AVAILABLE = "com.aconno.sensorics.ACTION_DATA_AVAILABLE"
        const val ACTION_DATA_AVAILABLE_FAIL = "com.aconno.sensorics.ACTION_DATA_AVAILABLE_FAIL"
        const val ACTION_GATT_CHAR_WRITE = "com.aconno.sensorics.ACTION_GATT_CHAR_WRITE"
        const val ACTION_GATT_CHAR_WRITE_FAIL = "com.aconno.sensorics.ACTION_GATT_CHAR_WRITE_FAIL"
        const val ACTION_GATT_DESCRIPTOR_READ = "com.aconno.sensorics.ACTION_GATT_DESCRIPTOR_READ"
        const val ACTION_GATT_DESCRIPTOR_READ_FAIL = "com.aconno.sensorics.ACTION_GATT_DESCRIPTOR_READ_FAIL"
        const val ACTION_GATT_DESCRIPTOR_WRITE = "com.aconno.sensorics.ACTION_GATT_DESCRIPTOR_WRITE"
        const val ACTION_GATT_DESCRIPTOR_WRITE_FAIL = "com.aconno.sensorics.ACTION_GATT_DESCRIPTOR_WRITE_FAIL"

        private const val SETTINGS_SERVICE_UUID = "cc52c000-9adb-4c37-bc48-376f5fee8851"
        private const val CACHE_SERVICE_UUID = "cc521000-9adb-4c37-bc48-376f5fee8851"
        private const val CACHE_CHARACTERISTIC_UUID = "cc521001-9adb-4c37-bc48-376f5fee8851"
    }
}