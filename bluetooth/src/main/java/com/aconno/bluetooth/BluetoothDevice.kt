package com.aconno.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import com.aconno.bluetooth.tasks.Task
import java.util.*

interface BluetoothDevice {
    var characteristicMap: Map<UUID, BluetoothGattCharacteristic>
    fun connect(autoConnect: Boolean, callback: BluetoothGattCallback? = null)
    fun addBluetoothGattCallback(callback: BluetoothGattCallback): Boolean
    fun removeBluetoothGattCallback(callback: BluetoothGattCallback): Boolean
    fun addTasksCompleteListener(listener: TasksCompleteListener): Boolean
    fun removeTasksCompleteListener(listener: TasksCompleteListener): Boolean
    fun queueTask(task: Task)
    fun queueTasks(tasks: List<Task>)
    fun addCharacteristicChangedListener(
        uuid: UUID,
        characteristicChangedListener: CharacteristicChangedListener
    )

    fun removeCharacteristicChangedListener(characteristicChangedListener: CharacteristicChangedListener): Boolean
    fun insertTask(task: Task)
    fun disconnect()
    fun setCharacteristicNotification(uuid: UUID, enable: Boolean)
    var queue: Deque<Task>
}


interface TaskCallback {
    fun onError(characteristic: BluetoothGattCharacteristic, error: Int)
}

interface WriteCallback : TaskCallback {
    fun onSuccess(characteristic: BluetoothGattCharacteristic)
}

interface ReadCallback : TaskCallback {
    fun onSuccess(characteristic: BluetoothGattCharacteristic, value: ByteArray)
}