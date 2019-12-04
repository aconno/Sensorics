package com.aconno.sensorics.device.beacon.v2

import android.content.Context
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.v2.arbitrarydata.ArbitraryData
import com.aconno.sensorics.device.beacon.v2.arbitrarydata.ArbitraryDataImpl
import com.aconno.sensorics.device.beacon.v2.parameters.ParametersImpl
import com.aconno.sensorics.device.beacon.v2.slots.SlotsImpl
import com.aconno.sensorics.device.bluetooth.tasks.BulkCharacteristicReadTask
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.sensorics.device.bluetooth.tasks.GenericTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateRequestCallback
import com.aconno.sensorics.device.bluetooth.tasks.lock.LockStateTask
import com.aconno.sensorics.device.bluetooth.tasks.lock.PasswordWriteTask
import com.aconno.sensorics.domain.UUIDProvider
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.domain.scanning.Task

/**
 * Beacon device class
 *
 * @property device ble device to use for this device
 * @property name device name TODO: remove
 */
class BeaconImpl(
    context: Context,
    private val taskProcessor: BluetoothTaskProcessor,
    var name: String? = ""
) : Beacon(context, taskProcessor) {
    override var slots: Slots = SlotsImpl(0)

    override val parameters: Parameters = ParametersImpl()

    override var arbitraryData: ArbitraryData = ArbitraryDataImpl()

    override fun unlock(password: String, callback: LockStateRequestCallback) {
        taskProcessor.queueTask(PasswordWriteTask(password, callback))
    }

    override fun requestDeviceLockStatus(callback: LockStateRequestCallback) {
        taskProcessor.queueTask(LockStateTask(callback))
    }

    override fun read(onDoneTask: GenericTask?) {
        val tasks = mutableListOf<Task>(object : BulkCharacteristicReadTask(
            name = "Parameter Bulk Read Task",
            serviceUUID = PARAMETER_SERVICE_UUID,
            characteristicUUID = PARAMETER_BULK_UUID
        ) {
            override fun onSuccess(value: ByteArray) {
                parameters.fromBytes(value)
                arbitraryData = ArbitraryDataImpl()
                this.taskQueue.add(arbitraryData.read(taskProcessor))
            }
        }, object : BulkCharacteristicReadTask(
            name = "Slot Bulk Read Task",
            serviceUUID = SLOT_SERVICE_UUID,
            characteristicUUID = SLOT_BULK_UUID
        ) {
            override fun onSuccess(value: ByteArray) {
                val slotCount: Int = ValueConverters.UINT32.deserialize(value, 4).toInt()

                slots = SlotsImpl(slotCount)
                slots.fromBytes(value)
            }
        }).also { list ->
            onDoneTask?.let { task ->
                list.add(task)
            }
        }.toList()
        taskProcessor.queueTasks(tasks)
    }

    override fun write(full: Boolean, onDoneTask: GenericTask?) {
        val tasks = mutableListOf<Task>(
            object : CharacteristicWriteTask(
                name = "Parameter Bulk Write Task",
                serviceUUID = PARAMETER_SERVICE_UUID,
                characteristicUUID = PARAMETER_BULK_UUID,
                value = parameters.toBytes()
            ) {
                override fun onSuccess() {
                }
            },
            object : CharacteristicWriteTask(
                name = "Slot Bulk Write Task",
                serviceUUID = SLOT_SERVICE_UUID,
                characteristicUUID = SLOT_BULK_UUID,
                value = slots.toBytes()
            ) {
                override fun onSuccess() {
                }
            }
        )
        if (full) tasks += listOf(
            object : CharacteristicWriteTask(
                name = "Parameter Flash Write",
                serviceUUID = PARAMETER_SERVICE_UUID,
                characteristicUUID = PARAMETER_SAVE_UUID,
                value = byteArrayOf(0xFF.toByte())
            ) {
                override fun onSuccess() {
                }
            },
            object : CharacteristicWriteTask(
                name = "Slot Flash Write",
                serviceUUID = SLOT_SERVICE_UUID,
                characteristicUUID = SLOT_SAVE_UUID,
                value = byteArrayOf(0xFF.toByte())
            ) {
                override fun onSuccess() {
                }
            }
        )

        onDoneTask?.let { task ->
            tasks.add(task)
        }

        taskProcessor.queueTasks(tasks)
        arbitraryData.write(taskProcessor, full)
    }

    companion object {
        val SLOT_SERVICE_UUID = UUIDProvider.provideFullUUID("B000")
        val SLOT_SAVE_UUID = UUIDProvider.provideFullUUID("B001")
        val SLOT_BULK_UUID = UUIDProvider.provideFullUUID("B002")
        val PARAMETER_SERVICE_UUID = UUIDProvider.provideFullUUID("C000")
        val PARAMETER_SAVE_UUID = UUIDProvider.provideFullUUID("C001")
        val PARAMETER_BULK_UUID = UUIDProvider.provideFullUUID("C002")
    }
}