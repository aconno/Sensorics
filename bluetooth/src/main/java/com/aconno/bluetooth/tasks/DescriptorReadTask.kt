package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import com.aconno.bluetooth.ReadCallback
import java.util.*

abstract class DescriptorReadTask(
    characteristic: UUID,
    val descriptor: UUID,
    override var retriesAllowed: Int = RETRIES_ALLOWED
) : Task(characteristic) {
    lateinit var realDescriptor: BluetoothGattDescriptor

    constructor(
        characteristic: BluetoothGattCharacteristic,
        descriptor: BluetoothGattDescriptor,
        callback: ReadCallback,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, descriptor.uuid, retriesAllowed = retriesAllowed)

    abstract fun onSuccess(value: ByteArray)
}
