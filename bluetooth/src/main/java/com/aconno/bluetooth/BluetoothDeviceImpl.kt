package com.aconno.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.*
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.aconno.bluetooth.beacon.isReadable
import com.aconno.bluetooth.beacon.isWriteable
import com.aconno.bluetooth.tasks.*
import timber.log.Timber
import java.util.*


val BLE_TAG = Timber.tag("BLE-Status")
const val MAX_PACKET_SIZE: Int = 20

class GattRequestFailedException : Exception("Gatt request failed...")

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class GattErrorException(val error: Int) : Exception("Gatt error occurred: $error")

class BluetoothDeviceImpl(
    val context: Context,
    val device: android.bluetooth.BluetoothDevice
) : BluetoothDevice, android.bluetooth.BluetoothGattCallback() {

    /**
     * Bluetooth Gatt connection
     */
    private var gatt: BluetoothGatt? = null
    /**
     * Current connection state
     */
    private var connectionState = STATE_DISCONNECTED
    /**
     * Characteristic map for easier access
     */
    override lateinit var characteristicMap: Map<UUID, BluetoothGattCharacteristic>
    /**
     * Task queue
     */
    override var queue: Deque<Task> = ArrayDeque()


    /**
     * Bluetooth Gatt state change listener list
     */
    private var callbacks: MutableSet<BluetoothGattCallback> = mutableSetOf()
    /**
     * Task complete listener list (triggers when a batch of tasks complete)
     */
    private var listeners: MutableSet<TasksCompleteListener> = mutableSetOf()
    /**
     * Characteristic Changed Listener Map (Characteristic UUID -> Listener List)
     */
    private var characteristicChangedListenerMap: MutableMap<UUID, MutableSet<CharacteristicChangedListener>> =
        mutableMapOf()

    /**
     * Connects to the device optionally using the specified callback
     */
    override fun connect(autoConnect: Boolean, callback: BluetoothGattCallback?) {
        if (callback != null && !callbacks.contains(callback)) callbacks.add(callback)
        gatt = device.connectGatt(context, autoConnect, this)
    }

    /**
     * Disconnects the device
     */
    override fun disconnect() {
        gatt?.disconnect() ?: onConnectionStateChange(gatt, 0, BluetoothProfile.STATE_DISCONNECTED)
    }

    /**
     * Gatt connection state change listener
     */
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        connectionState = newState
        when (newState) {
            STATE_CONNECTED -> {
                callbacks.forEach { it.onDeviceConnected(this) }
                gatt?.discoverServices()
            }
            STATE_DISCONNECTED -> callbacks.forEach { it.onDeviceDisconnected(this) }
            STATE_CONNECTING -> callbacks.forEach { it.onDeviceConnecting(this) }
            STATE_DISCONNECTING -> callbacks.forEach { it.onDeviceDisconnecting(this) }
        }
    }

    /**
     * Gets characteristic by UUID and throws an exception if it doesn't exist
     * @param uuid the uuid
     * @return the characteristic
     */
    private fun getChar(uuid: UUID): BluetoothGattCharacteristic =
        characteristicMap.getOrElse(uuid) {
            throw Exception("Characteristic with UUID $uuid does not exist")
        }

    /**
     * Prepares the tasks (sets actual characteristic objects etc...)
     */
    private fun prepareTask(task: Task): Task {
        return when (task) {
            is GenericTask -> task
            is CharacteristicTask -> task.apply { characteristic = getChar(characteristicUUID) }
            is DescriptorTask -> task.apply {
                descriptor = getChar(characteristicUUID).getDescriptor(descriptorUUID)
            }
            else -> throw NotImplementedError()
        }
    }

    /**
     * Queues up a task
     */
    override fun queueTask(task: Task) {
        queue.offer(prepareTask(task))
        processQueue()
    }

    /**
     * Qeues up multiple tasks
     */
    override fun queueTasks(tasks: List<Task>) {
        tasks.forEach { queue.offer(prepareTask(it)) }
        processQueue()
    }

    /**
     * Inserts task in front of queue
     */
    override fun insertTask(task: Task) {
        queue.offerFirst(prepareTask(task))
    }

    /**
     * Starts processing the task queue
     */
    private fun processQueue() {
        queue.peek()?.let { task ->
            if (task.active) return@let

            gatt?.takeIf { connectionState == BluetoothProfile.STATE_CONNECTED }?.let { gatt ->
                task.active = true

                val success: Boolean = when (task) {
                    is GenericTask -> {
                        BLE_TAG.i("Executing generic task")
                        try {
                            task.execute()
                            queueTasks(task.taskQueue.toList())
                            true
                        } catch (e: Exception) {
                            task.internalException = e
                            false
                        }
                    }
                    is CharacteristicReadTask -> {
                        BLE_TAG.i("Reading data from characteristic ${task.characteristicUUID}")
                        gatt.readCharacteristic(task.characteristic)
                    }
                    is CharacteristicWriteTask -> {
                        BLE_TAG.i("Writing ${task.value.size} bytes to characteristic ${task.characteristicUUID}")
                        BLE_TAG.i("Data: ${task.value.toHex()}")
                        gatt.writeCharacteristic(task.characteristic!!.apply {
                            value = task.value.copyOfRange(
                                0,
                                Math.min(task.value.size, MAX_PACKET_SIZE)
                            )
                            writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        })
                    }
                    is DescriptorReadTask -> {
                        BLE_TAG.i("Reading data from descriptor ${task.descriptor!!.uuid} on characteristic ${task.descriptor!!.characteristic}")
                        gatt.readDescriptor(task.descriptor)
                    }
                    is DescriptorWriteTask -> {
                        BLE_TAG.i("Writing ${task.value.size} bytes to descriptor ${task.descriptor} on characteristic ${task.descriptor!!.characteristic}")
                        BLE_TAG.i("Data: ${task.value.toHex()}")
                        gatt.writeDescriptor(task.descriptor!!.apply {
                            value = task.value
                        })
                    }
                    else -> {
                        throw NotImplementedError()
                    }
                }

                if (task is GenericTask) {
                    try {
                        if (!success) task.onError(this, task.internalException)
                        queue.remove()
                        processQueue()
                    } catch (e: Exception) {
                        disconnect()
                    }
                } else {
                    if (!success) {
                        try {
                            task.onError(this, GattRequestFailedException())
                            queue.remove()
                            processQueue()
                        } catch (e: Exception) {
                            disconnect()
                        }
                    }
                }
            } ?: disconnect()
        } ?: listeners.removeAll {
            it.onAllTasksCompleted()
            it.finishable
        }
    }

    override fun setCharacteristicNotification(uuid: UUID, enable: Boolean) {
        queueTask(object : DescriptorWriteTask(
            characteristicUUID = uuid,
            descriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"),
            value = byteArrayOf(if (enable) 0x01 else 0x00, 0x00)
        ) {
            override fun onSuccess() {
                gatt?.setCharacteristicNotification(this.descriptor!!.characteristic, enable)
            }

            override fun onError(device: BluetoothDevice, e: Exception) {
                TODO("not implemented")
            }
        })
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        Timber.e("On services discovered")
        gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
        this.characteristicMap =
            gatt.services.flatMap { it.characteristics }.associateBy { it.uuid }

        BLE_TAG.i("${gatt.services.size} services with ${characteristicMap.size} characteristics discovered!")

        BLE_TAG.d("Services:")
        gatt.services.forEach { s ->
            BLE_TAG.d("\t${s.uuid}:")
            s.characteristics.forEach { c ->
                BLE_TAG.d("\t\t${c.uuid}: ${if (c.isReadable()) "READ" else "NO READ"} / ${if (c.isWriteable()) "WRITE" else "NO WRITE"}")
            }
        }

        callbacks.forEach { it.onServicesDiscovered(this) }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        with(queue.remove() as CharacteristicReadTask) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("Read data from ${characteristic.uuid}")
                BLE_TAG.i("Data (${characteristic.value.size} bytes): ${characteristic.value.toHex()}")
                this.onSuccess(characteristic.value)
            } else {
                BLE_TAG.e("Failed to read characteristic ${characteristic.uuid} with status $status")

                try {
                    this.onError(this@BluetoothDeviceImpl, GattErrorException(status))
                } catch (e: Exception) {
                    disconnect()
                }
            }
            listeners.forEach { it.onTaskComplete(queue.size) }
            taskQueue.reversed().forEach { insertTask(it) }
        }
        processQueue()
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        with(queue.remove() as CharacteristicWriteTask) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("Written data to ${characteristic.uuid}")
                BLE_TAG.i("Data (${characteristic.value.size + bytesWritten}/$totalBytes bytes): ${characteristic.value.toHex()}")
                if (this.value.size > MAX_PACKET_SIZE) {
                    insertTask(object : CharacteristicWriteTask(
                        characteristic = characteristic,
                        value = value.copyOfRange(MAX_PACKET_SIZE, value.size),
                        totalBytes = totalBytes,
                        bytesWritten = bytesWritten + MAX_PACKET_SIZE
                    ) {
                        override fun onError(device: BluetoothDevice, e: Exception) {
                            this@with.onError(this@BluetoothDeviceImpl, e)
                        }

                        override fun onSuccess() {
                            this@with.onSuccess()
                        }
                    })
                } else {
                    this.onSuccess()
                    listeners.forEach { it.onTaskComplete(queue.size) }
                    taskQueue.reversed().forEach { insertTask(it) }
                }
                processQueue()
            } else {
                BLE_TAG.e("Failed to write to characteristic ${characteristic.uuid} with status $status!")
                try {
                    this.onError(this@BluetoothDeviceImpl, GattErrorException(status))
                    listeners.forEach { it.onTaskComplete(queue.size) }
                    taskQueue.reversed().forEach { insertTask(it) }
                    processQueue()
                } catch (e: Exception) {
                    disconnect()
                }
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        characteristicChangedListenerMap[characteristic.uuid]?.forEach {
            it.onCharacteristicChanged(characteristic, characteristic.value)
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ) {
        with(queue.remove() as DescriptorReadTask) {
            if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("Read data from descriptor ${descriptor.uuid} on characteristic ${descriptor.characteristic}")
                BLE_TAG.i("Data: ${descriptor.value.toHex()}")
                onSuccess(descriptor.value)
            } else {
                BLE_TAG.e("Failed to read descriptor ${descriptor.uuid} on characteristic ${descriptor.characteristic} with status $status")
                try {
                    this.onError(this@BluetoothDeviceImpl, GattErrorException(status))
                } catch (e: Exception) {
                    disconnect()
                    throw e
                }
            }
            listeners.forEach { it.onTaskComplete(queue.size) }
            taskQueue.reversed().forEach { insertTask(it) }
        }
        processQueue()
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ) {
        with(queue.remove() as DescriptorWriteTask) {
            if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS) {
                BLE_TAG.i("Written data to descriptor ${descriptor.uuid} on characteristic ${descriptor.characteristic}")
                BLE_TAG.i(" Data (${descriptor.value.size} bytes): ${descriptor.value.toHex()}")
                onSuccess()
            } else {
                BLE_TAG.e("Failed to write to descriptor ${descriptor.uuid} on characteristic ${descriptor.characteristic} with status $status")
                try {
                    this.onError(this@BluetoothDeviceImpl, GattErrorException(status))
                } catch (e: Exception) {
                    disconnect()
                    throw e
                }
            }
            listeners.forEach { it.onTaskComplete(queue.size) }
            taskQueue.reversed().forEach { insertTask(it) }
        }
        processQueue()
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        super.onReliableWriteCompleted(gatt, status)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyRead(gatt, txPhy, rxPhy, status)
    }

    override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status)
    }


    override fun addBluetoothGattCallback(callback: BluetoothGattCallback) = callbacks.add(callback)

    override fun removeBluetoothGattCallback(callback: BluetoothGattCallback) =
        callbacks.remove(callback)

    override fun addTasksCompleteListener(listener: TasksCompleteListener): Boolean =
        listeners.add(listener)

    override fun removeTasksCompleteListener(listener: TasksCompleteListener): Boolean =
        listeners.remove(listener)

    override fun addCharacteristicChangedListener(
        uuid: UUID,
        characteristicChangedListener: CharacteristicChangedListener
    ) {
        characteristicChangedListenerMap.getOrPut(uuid) {
            mutableSetOf()
        }.add(characteristicChangedListener)
    }

    override fun removeCharacteristicChangedListener(characteristicChangedListener: CharacteristicChangedListener): Boolean {
        characteristicChangedListenerMap.forEach { entry ->
            entry.value.remove(characteristicChangedListener).let { if (it) return it }
        }
        return false
    }

}