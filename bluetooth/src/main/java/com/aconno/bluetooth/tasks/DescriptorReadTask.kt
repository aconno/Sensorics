package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattDescriptor
import java.util.*

abstract class DescriptorReadTask(
    override var descriptor: BluetoothGattDescriptor? = null,
    override val descriptorUUID: UUID = descriptor!!.uuid,
    override val characteristicUUID: UUID = descriptor!!.characteristic.uuid
) : DescriptorTask(descriptor, descriptorUUID, characteristicUUID) {
    abstract fun onSuccess(value: ByteArray)
}

