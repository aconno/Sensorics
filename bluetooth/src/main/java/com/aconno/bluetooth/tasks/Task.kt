package com.aconno.bluetooth.tasks

import android.bluetooth.BluetoothGattCharacteristic
import com.aconno.bluetooth.TaskCallback
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

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

    companion object {
        const val RETRIES_ALLOWED = 5
    }
}