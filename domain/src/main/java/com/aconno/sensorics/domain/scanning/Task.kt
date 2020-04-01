package com.aconno.sensorics.domain.scanning

import java.util.*


interface Task {
    /**
     * Task name
     */
    val name: String
    /**
     * Marks if this task is currently being processed
     */
    var active: Boolean

    /**
     * Internal task queue, holds any tasks that should be executed exactly after this one
     */
    val taskQueue: Queue<Task>

    /**
     * Function you should implement if you want to execute something before the task execute
     * function gets called
     */
    fun onPreExecute()

    /**
     * Implement actual task execution logic here
     *
     * @param bluetooth Bluetooth instance
     * @return success status
     */
    fun execute(bluetooth: Bluetooth): Boolean

    /**
     * Gets called if an error occurs during execution
     *
     * @param bluetooth Bluetooth instance
     * @param e Exception that occurred
     */
    fun onError(bluetooth: Bluetooth, e: Exception)

}

interface MtuTask : Task {
    /**
     * Gets called after the new MTU value is acquired
     *
     * @param mtu
     */
    fun onSuccess(mtu: Int)

    fun onBluetoothSuccess(bluetooth: Bluetooth, mtu: Int)
}

interface ReadTask : Task {
    fun onSuccess(value: ByteArray)

    fun onBluetoothSuccess(bluetooth: Bluetooth, value: ByteArray)
}

interface WriteTask : Task {
    var value: ByteArray
    val bytesLeft: Int

    fun onSuccess()

    fun onBluetoothSuccess(bluetooth: Bluetooth)
}

interface CharacteristicTask : Task {
    val serviceUUID: UUID
    val characteristicUUID: UUID
}

interface DescriptorTask : Task {
    val serviceUUID: UUID
    val characteristicUUID: UUID
    val descriptorUUID: UUID
}

abstract class TaskBase(
    override var active: Boolean = false
) : Task {
    override val taskQueue: Queue<Task> = ArrayDeque()

    override fun onPreExecute() {}

    override fun onError(bluetooth: Bluetooth, e: Exception) {
        bluetooth.disconnect()
    }

    override fun toString(): String {
        return this.name
    }
}