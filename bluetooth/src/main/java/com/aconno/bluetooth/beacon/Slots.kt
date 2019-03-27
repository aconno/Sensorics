package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.*
import timber.log.Timber


val SLOT_INDEX_UUID = UUIDProvider.provideFullUUID("B001")
val SLOT_TYPE_UUID = UUIDProvider.provideFullUUID("B002")
val SLOT_DATA_UUID = UUIDProvider.provideFullUUID("B003")

class Slots(
        override val size: Int,
        val supportedSlots: Array<Slot.Type> = arrayOf()
) : ArrayList<Slot>(size), BleReadableWritable {

    init {
        repeat(size) { add(Slot()) }
    }

    override fun read(): List<Task> {
        return (0 until size).map { i -> SlotReadTask(i) }.toList()
    }

    override fun write(full: Boolean): List<Task> {
        return this@Slots.filterNotNull().mapIndexed { i, slot ->
            object : CharacteristicWriteTask(characteristicUUID = SLOT_INDEX_UUID, value = byteArrayOf(i.toByte())) {
                override fun onSuccess() {
                    slot.write(full).reversed().forEach { taskQueue.offer(it) }
                }

                override fun onError(device: BluetoothDevice, e: Exception) {
                    Timber.e("Failed to write slot id $i")
                    super.onError(device, e)
                }
            }
        }.toList() + listOf(object : CharacteristicWriteTask(characteristicUUID = SLOT_INDEX_UUID, value = byteArrayOf(size.toByte())) {
            override fun onSuccess() {
                Timber.i("Finalized Write of Slots")
            }
        })
    }

    inner class SlotReadTask(val slotIndex: Int) : CharacteristicWriteTask(characteristicUUID = SLOT_INDEX_UUID, value = byteArrayOf(slotIndex.toByte())) {
        override fun onSuccess() {
            taskQueue.offer(object : CharacteristicReadTask(characteristicUUID = SLOT_TYPE_UUID) {
                override fun onSuccess(value: ByteArray) {
                    this@Slots[slotIndex] = Slot(value).apply {
                        read().reversed().forEach { taskQueue.offer(it) }
                    }
                }

                override fun onError(device: BluetoothDevice, e: Exception) {
                    Timber.e("Failed to read slot type for slot $slotIndex")
                    super.onError(device, e)
                }
            })
        }

        override fun onError(device: BluetoothDevice, e: Exception) {
            Timber.e("Failed to write slot id $slotIndex")
            super.onError(device, e)
        }
    }
}