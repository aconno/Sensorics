package com.aconno.sensorics.domain.scanning

import com.aconno.sensorics.domain.model.GattCallbackPayload
import io.reactivex.functions.Consumer

interface BluetoothTaskProcessor : Consumer<GattCallbackPayload> {
    fun queueTask(task: Task): Boolean
    fun queueTasks(tasks: List<Task>): Boolean
}