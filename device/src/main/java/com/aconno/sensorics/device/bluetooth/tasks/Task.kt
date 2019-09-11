package com.aconno.sensorics.device.bluetooth.tasks

import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.migrate.toCompactHex
import com.aconno.sensorics.domain.scanning.*
import timber.log.Timber
import java.util.*
import java.util.zip.CRC32
import kotlin.math.min

abstract class GenericTask(override val name: String = "Generic Task") : TaskBase() {
    abstract fun onSuccess()
}

abstract class MtuRequestTask(
    private val mtu: Int,
    final override val name: String = "Mtu Request Task"
) : TaskBase(), MtuTask {
    final override fun execute(bluetooth: Bluetooth): Boolean {
        Timber.d("Requesting Mtu: $mtu")
        return bluetooth.requestMtu(mtu)
    }

    final override fun onBluetoothSuccess(bluetooth: Bluetooth, mtu: Int) {
        this.onSuccess(mtu)
    }
}

abstract class ReadTaskBase : TaskBase(), ReadTask {
    final override fun execute(bluetooth: Bluetooth): Boolean {
        return this.read(bluetooth)
    }

    abstract fun read(bluetooth: Bluetooth): Boolean
}

abstract class CharacteristicReadTaskBase : ReadTaskBase(), Task, CharacteristicTask, ReadTask {
    final override fun read(bluetooth: Bluetooth): Boolean {
        Timber.d("Reading data from characteristic: $characteristicUUID (service: $serviceUUID)")
        return bluetooth.readCharacteristic(serviceUUID, characteristicUUID)
    }
}

abstract class CharacteristicReadTask(
    final override val serviceUUID: UUID,
    final override val characteristicUUID: UUID,
    final override val name: String = "Characteristic Read Task"
) : CharacteristicReadTaskBase() {
    final override fun onBluetoothSuccess(bluetooth: Bluetooth, value: ByteArray) {
        this.onSuccess(value)
    }
}


abstract class BulkCharacteristicReadTask(
    final override val serviceUUID: UUID,
    final override val characteristicUUID: UUID,
    final override val name: String = "Bulk Characteristic Read Task"
) : CharacteristicReadTaskBase() {
    val data: MutableList<Byte> = mutableListOf()

    val totalSize: Long
        get() = data.takeIf { it.size >= 4 }?.toByteArray()?.let {
            UINT32.deserialize(it, 0)
        } ?: -1

    final override fun onBluetoothSuccess(bluetooth: Bluetooth, value: ByteArray) {
        this.taskQueue.remove(this)

        data.addAll(value.toList())

        if (data.size < totalSize) {
            this.taskQueue.offer(this.apply { active = false })
        } else {
            this.onSuccess(this.data.toByteArray())
        }
    }
}

abstract class DescriptorReadTask(
    final override val serviceUUID: UUID,
    final override val characteristicUUID: UUID,
    final override val descriptorUUID: UUID,
    override val name: String = "Descriptor Read Task"
) : ReadTaskBase(), Task, DescriptorTask, ReadTask {
    final override fun read(bluetooth: Bluetooth): Boolean {
        Timber.d("""
            Reading data from descriptor: \
            $descriptorUUID (characteristic: $characteristicUUID (service: $serviceUUID))
            """)
        return bluetooth.readDescriptor(serviceUUID, characteristicUUID, descriptorUUID)
    }

    override fun onBluetoothSuccess(bluetooth: Bluetooth, value: ByteArray) {
        this.onSuccess(value)
    }
}

abstract class WriteTaskBase : TaskBase(), WriteTask {
    private var bytesWritten: Int = 0

    override val bytesLeft: Int
        get() = totalBytes - bytesWritten

    private val totalBytes: Int
        get() = value.size

    final override fun execute(bluetooth: Bluetooth): Boolean {
        val data = value.copyOfRange(
            bytesWritten,
            min(bytesWritten + bluetooth.mtu - (bluetooth.mtu % 20), totalBytes)
        )

        return write(bluetooth, data).also {
            if (it) {
                bytesWritten += data.size
            }
        }
    }

    override fun onBluetoothSuccess(bluetooth: Bluetooth) {
        this.taskQueue.remove(this)

        if (bytesLeft > 0) {
            this.taskQueue.offer(this.apply { active = false })
        } else {
            this.onSuccess()
        }
    }

    abstract fun write(bluetooth: Bluetooth, data: ByteArray): Boolean
}

abstract class CharacteristicWriteTask(
    final override val serviceUUID: UUID,
    final override val characteristicUUID: UUID,
    final override var value: ByteArray,
    override val name: String = "Characteristic Write Task"
) : WriteTaskBase(), Task, CharacteristicTask, WriteTask {
    final override fun write(bluetooth: Bluetooth, data: ByteArray): Boolean {
        Timber.d("""
            Writing data to characteristic: \
            ${value.toCompactHex()} -> $characteristicUUID (service: $serviceUUID)
            """)

        return bluetooth.writeCharacteristic(
            serviceUUID,
            characteristicUUID,
            "BYTE",
            data
        )
    }
}

abstract class Crc32ValidatedCharacteristicWriteTask(
    serviceUUID: UUID,
    characteristicUUID: UUID,
    value: ByteArray,
    override val name: String = "CRC32 Validated Characteristic Write Task"
) : CharacteristicWriteTask(
    serviceUUID,
    characteristicUUID,
    value.let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
)

abstract class DescriptorWriteTask(
    final override val serviceUUID: UUID,
    final override val characteristicUUID: UUID,
    final override val descriptorUUID: UUID,
    final override var value: ByteArray,
    override val name: String = "Descriptor Write Task"
) : WriteTaskBase(), Task, DescriptorTask, WriteTask {
    final override fun write(bluetooth: Bluetooth, data: ByteArray): Boolean {
        Timber.d("""
            Writing data to descriptor: \
            ${data.toCompactHex()} -> \
            $descriptorUUID (characteristic: $characteristicUUID (service: $serviceUUID))
            """)

        return bluetooth.writeDescriptor(
            serviceUUID,
            characteristicUUID,
            descriptorUUID,
            "BYTE",
            data
        )
    }
}