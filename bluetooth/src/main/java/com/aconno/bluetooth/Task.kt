package com.aconno.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import java.util.*


const val RETRIES_ALLOWED = 5

abstract class Task(open val name: String = "Task", var active: Boolean = false) {
    val taskQueue: Queue<Task> = ArrayDeque<Task>()

    open fun onError(device: BluetoothDevice, e: Exception) {
        device.disconnect()
    }

    override fun toString(): String {
        return this.name
    }
}

abstract class GenericTask(override val name: String = "Generic Task") : Task() {
    lateinit var internalException: Exception
    abstract fun execute()
}

abstract class CharacteristicTask(
        override val name: String = "Characteristic Task",
        open var characteristic: BluetoothGattCharacteristic?,
        open val characteristicUUID: UUID = characteristic!!.uuid
) : Task()

abstract class CharacteristicReadTask(
        override val name: String = "Characteristic Read Task",
        override var characteristic: BluetoothGattCharacteristic? = null,
        override val characteristicUUID: UUID = characteristic!!.uuid
) : CharacteristicTask(name, characteristic, characteristicUUID) {
    abstract fun onSuccess(value: ByteArray)
}

abstract class CharacteristicWriteTask(
        override val name: String = "Characteristic Write Task",
        override var characteristic: BluetoothGattCharacteristic? = null,
        override val characteristicUUID: UUID = characteristic!!.uuid,
        val value: ByteArray,
        val totalBytes: Int = value.size,
        val bytesWritten: Int = 0
) : CharacteristicTask(name, characteristic, characteristicUUID) {
    abstract fun onSuccess()
}

abstract class DescriptorTask(
        override val name: String = "Descriptor Task",
        open var descriptor: BluetoothGattDescriptor?,
        open val descriptorUUID: UUID = descriptor!!.uuid,
        open val characteristicUUID: UUID = descriptor!!.characteristic.uuid
) : Task()

abstract class DescriptorReadTask(
        override val name: String = "Descriptor Read Task",
        override var descriptor: BluetoothGattDescriptor? = null,
        override val descriptorUUID: UUID = descriptor!!.uuid,
        override val characteristicUUID: UUID = descriptor!!.characteristic.uuid
) : DescriptorTask(name, descriptor, descriptorUUID, characteristicUUID) {
    abstract fun onSuccess(value: ByteArray)
}

abstract class DescriptorWriteTask(
        override val name: String = "Descriptor Write Task",
        override var descriptor: BluetoothGattDescriptor? = null,
        override val descriptorUUID: UUID = descriptor!!.uuid,
        override val characteristicUUID: UUID = descriptor!!.characteristic.uuid,
        val value: ByteArray
) : DescriptorTask(name, descriptor, descriptorUUID, characteristicUUID) {
    abstract fun onSuccess()
}
