package com.aconno.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*
import java.util.concurrent.LinkedBlockingDeque


const val RETRIES_ALLOWED = 5

abstract class Task(
    val characteristic: UUID,
    var active: Boolean = false,
    open var retriesAllowed: Int = RETRIES_ALLOWED
) {
    lateinit var realCharacteristic: BluetoothGattCharacteristic
    val taskQueue: Queue<Task> = LinkedBlockingDeque()

    constructor(
        characteristic: BluetoothGattCharacteristic,
        callback: TaskCallback,
        active: Boolean = false,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, active, retriesAllowed) {
        this.realCharacteristic = characteristic
    }

    abstract fun onError(error: Int)
}

abstract class ReadTask(
    characteristic: UUID,
    override var retriesAllowed: Int = RETRIES_ALLOWED
) : Task(characteristic) {
    constructor(
        characteristic: BluetoothGattCharacteristic,
        callback: ReadCallback,
        retriesAllowed: Int = RETRIES_ALLOWED
    ) : this(characteristic.uuid, retriesAllowed = retriesAllowed)

    abstract fun onSuccess(value: ByteArray)
}

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
