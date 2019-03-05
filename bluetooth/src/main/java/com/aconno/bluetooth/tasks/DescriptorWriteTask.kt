package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import java.util.*

abstract class DescriptorWriteTask(
    characteristic: UUID,
    val descriptor: UUID,
    val value: ByteArray,
    override var retriesAllowed: Int = RETRIES_ALLOWED
) : Task(characteristic) {
    lateinit var realDescriptor: BluetoothGattDescriptor

    constructor(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        value: ByteArray,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, descriptor.uuid, value, retriesAllowed = retriesAllowed)

    abstract fun onSuccess()
}
