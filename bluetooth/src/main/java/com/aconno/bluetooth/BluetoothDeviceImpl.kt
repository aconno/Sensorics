package com.aconno.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.aconno.bluetooth.tasks.ReadTask
import com.aconno.bluetooth.tasks.Task
import com.aconno.bluetooth.tasks.WriteTask
import timber.log.Timber
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class BluetoothDeviceImpl(
    val context: Context,
    val device: android.bluetooth.BluetoothDevice
) : BluetoothDevice, android.bluetooth.BluetoothGattCallback() {
    val BLE_TAG = Timber.tag("BLE-Status")
    override fun disconnect() {
        gatt?.disconnect()
    }

    var callbacks: MutableList<BluetoothGattCallback> = mutableListOf()
    var listeners: MutableList<TasksCompleteListener> = mutableListOf()
    var gatt: BluetoothGatt? = null


    override var services: MutableList<BluetoothGattService> = mutableListOf()
    var connectionState = STATE_DISCONNECTED

    override var queue: Deque<Task> = LinkedBlockingDeque()
    var characteristicChangedListenerMap: MutableMap<UUID, MutableList<CharacteristicChangedListener>> =
        mutableMapOf()

    override fun connect(autoConnect: Boolean, callback: BluetoothGattCallback?) {
        if (callback != null && !callbacks.contains(callback)) callbacks.add(callback)
        gatt = device.connectGatt(context, autoConnect, this)
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        connectionState = newState
        when (newState) {
            STATE_CONNECTED -> {
                callbacks.forEach { it.onDeviceConnected(this) }
                gatt.discoverServices()
            }
            STATE_DISCONNECTED -> callbacks.forEach { it.onDeviceDisconnected(this) }
            STATE_CONNECTING -> callbacks.forEach { it.onDeviceConnecting(this) }
            STATE_DISCONNECTING -> callbacks.forEach { it.onDeviceDisconnecting(this) }
        }
    }

    override fun addBluetoothGattCallback(callback: BluetoothGattCallback) = callbacks.add(callback)
    override fun removeBluetoothGattCallback(callback: BluetoothGattCallback) =
        callbacks.remove(callback)

    override fun addTasksCompleteListener(listener: TasksCompleteListener): Boolean =
        listeners.add(listener)

    override fun removeTasksCompleteListener(listener: TasksCompleteListener): Boolean =
        listeners.remove(listener)

    override fun queueTask(task: Task) {
        queue.offer(prepareTask(task))
        processQueue()
    }

    override fun queueTasks(tasks: List<Task>) {
        tasks.forEach { queue.offer(prepareTask(it)) }
        processQueue()
    }

    override fun insertTask(task: Task) {
        queue.offerFirst(prepareTask(task))
    }

    private fun prepareTask(task: Task): Task {
        return task.apply {
            realCharacteristic =
                services.flatMap { it.characteristics }.find { it.uuid == this.characteristic }
                    ?: throw IllegalArgumentException("Invalid characteristic used ${this.characteristic}!")
        }
    }

    override fun addCharacteristicChangedListener(
        uuid: UUID,
        characteristicChangedListener: CharacteristicChangedListener
    ) {
        characteristicChangedListenerMap.getOrPut(uuid) {
            mutableListOf()
        }.add(characteristicChangedListener)
    }

    override fun removeCharacteristicChangedListener(characteristicChangedListener: CharacteristicChangedListener): Boolean {
        characteristicChangedListenerMap.forEach { entry ->
            entry.value.remove(characteristicChangedListener).let { if (it) return it }
        }
        return false
    }

    private fun processQueue() {
        queue.peek()?.let { task ->
            if (!task.active) gatt.let { gatt ->
                if (connectionState == STATE_CONNECTED && gatt != null) {
                    task.active = true
                    when (task) {
                        is ReadTask -> {
                            BLE_TAG.i("Reading from ${task.realCharacteristic.uuid}...")
                            if (!gatt.readCharacteristic(task.realCharacteristic)) {
                                task.onError(GATT_FAILED_TO_REQUEST)
                                queue.remove()
                                processQueue()
                            }
                        }
                        is WriteTask -> {
                            BLE_TAG.i("Writing ${task.value.size} bytes at ${task.realCharacteristic.uuid}... Data: ${task.value.toHex()}")
                            if (!gatt.writeCharacteristic(task.realCharacteristic.apply {
                                    value =
                                        if (task.value.size > MAX_PACKET_SIZE) task.value.copyOfRange(
                                            0,
                                            MAX_PACKET_SIZE
                                        ) else task.value
                                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                                })) {
                                task.onError(GATT_FAILED_TO_REQUEST)
                                queue.remove()
                                processQueue()
                            }
                        }
                        else -> throw NotImplementedError()
                    }
                    task
                } else {
                    task.onError(GATT_NOT_CONNECTED)
                    queue.remove()
                }
            }
        } ?: listeners.removeAll {
            it.onAllTasksCompleted()
            it.finishable
        }
    }

    override fun setCharacteristicNotification(uuid: UUID, enable: Boolean) {
        gatt?.let { gatt ->
            gatt.services.flatMap { it.characteristics }.find { it.uuid == uuid }?.let {
                gatt.setCharacteristicNotification(it, enable)
                val descriptor = it.descriptors[0]
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor) // TODO: Fix make tasks
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)

        services = gatt.services
        gatt.services.flatMap { it.characteristics }
            .associateBy { it.uuid.toString() } // TODO: Get Real Characteristic Faster
        BLE_TAG.i("${gatt.services.size} services with ${gatt.services.flatMap { it.characteristics }.size} characteristics discovered!")
        BLE_TAG.d("Services:")
        gatt.services.forEach { s ->
            BLE_TAG.d("\t${s.uuid}:")
            s.characteristics.forEach { c ->
                BLE_TAG.d(
                    "\t\t${c.uuid}: ${if (c.permissions.and(0x01) != 0) "READ" else "NO READ"} / ${if (c.permissions.and(
                            0x10
                        ) != 0
                    ) "WRITE" else "NO WRITE"}"
                )
            }
        }
        callbacks.forEach { it.onServicesDiscovered(this) }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        with(queue.remove() as ReadTask) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("Read ${characteristic.value.size} bytes from ${characteristic.uuid}! Data: ${characteristic.value.toHex()}")
                this.onSuccess(characteristic.value)
                Timber.e(listeners.size.toString())
                listeners.forEach { it.onTaskComplete(queue.size) }
                this.taskQueue.reversed().forEach { insertTask(it) }
            } else {
                BLE_TAG.e("Reading failed for ${characteristic.uuid} with status $status!")
                this.onError(status)
                this.taskQueue.reversed().forEach { insertTask(it) }
            }
        }
        processQueue()
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        with(queue.remove() as WriteTask) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("${characteristic.value.size + this.bytesWritten}/${this.totalBytes} bytes written at ${characteristic.uuid}! Data: ${characteristic.value.toHex()}")
                if (this.value.size > MAX_PACKET_SIZE) {
                    insertTask(object : WriteTask(
                        characteristic = characteristic,
                        value = value.copyOfRange(MAX_PACKET_SIZE, value.size),
                        totalBytes = totalBytes,
                        bytesWritten = bytesWritten + MAX_PACKET_SIZE
                    ) {
                        override fun onError(error: Int) {
                            onError(error)
                        }

                        override fun onSuccess() {
                            this@with.onSuccess()
                        }
                    })
                } else {
                    this.onSuccess()
                    listeners.forEach { it.onTaskComplete(queue.size) }
                    this.taskQueue.reversed().forEach { insertTask(it) }
                }
            } else {
                BLE_TAG.e("Writing failed for ${characteristic.uuid} with status $status!")
                this.taskQueue.reversed().forEach { insertTask(it) }
                this.onError(status)
            }
        }
        processQueue()
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        characteristicChangedListenerMap[characteristic.uuid]?.forEach {
            it.onCharacteristicChanged(characteristic, characteristic.value)
        }
    }

    companion object {
        const val GATT_FAILED_TO_REQUEST: Int = -4
        const val GATT_ERROR: Int = -3
        const val GATT_MAX_RETRIES: Int = -2
        const val GATT_NOT_CONNECTED: Int = -1
        const val GATT_SUCCESS: Int = 0
        const val MAX_PACKET_SIZE: Int = 20
    }
}