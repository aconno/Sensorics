package com.aconno.bluetooth.testing

import java.util.*


class BluetoothGattCharacteristicImpl(
    val characteristic: android.bluetooth.BluetoothGattCharacteristic,
    override val service: BluetoothGattService
) : BluetoothGattCharacteristic {
    override val descriptors: MutableList<BluetoothGattDescriptor> =
        characteristic.descriptors.map { BluetoothGattDescriptorImpl(it, this) }.toMutableList()
    override val instanceId: Int = characteristic.instanceId
    override val permissions: Int = characteristic.permissions
    override val properties: Int = characteristic.properties
    override val uuid: UUID = characteristic.uuid
    override var value: ByteArray
        get() = characteristic.value
        set(value) {
            characteristic.value = value
        }
    override var writeType: Int
        get() = characteristic.writeType
        set(value) {
            characteristic.writeType = value
        }

    override fun write(value: ByteArray?) {
        value?.let { this.value = it }
    }
}