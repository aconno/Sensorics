package com.aconno.bluetooth.tasks

import com.aconno.bluetooth.BluetoothDevice
import java.util.*

abstract class Task(var active: Boolean = false) {
    val taskQueue: Queue<Task> = ArrayDeque<Task>()

    open fun onError(device: BluetoothDevice, e: Exception) {
        device.disconnect()
    }

    companion object {
        const val RETRIES_ALLOWED = 5
    }
}