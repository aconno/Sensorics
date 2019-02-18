package com.aconno.bluetooth.testing

import java.util.*


class BluetoothGattDescriptorImpl(
    val descriptor: android.bluetooth.BluetoothGattDescriptor,
    override val characteristic: BluetoothGattCharacteristic
) : BluetoothGattDescriptor {
    override val permissions: Int = descriptor.permissions
    override val uuid: UUID = descriptor.uuid
    override var value: ByteArray
        get() = descriptor.value
        set(value) {
            descriptor.value = value
        }
}