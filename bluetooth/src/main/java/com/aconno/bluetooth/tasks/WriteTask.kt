package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

abstract class WriteTask(
    characteristic: UUID,
    val value: ByteArray,
    val totalBytes: Int = value.size,
    val bytesWritten: Int = 0,
    override var retriesAllowed: Int = RETRIES_ALLOWED
) : Task(characteristic) {
    constructor(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        totalBytes: Int = value.size,
        bytesWritten: Int = 0,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, value, totalBytes, bytesWritten, retriesAllowed = retriesAllowed)

    abstract fun onSuccess()
}