package com.aconno.sensorics.device.beacon.v2.slots

import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.v2.slots.SlotImpl.Config
import com.aconno.sensorics.domain.migrate.*
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import timber.log.Timber
import java.util.zip.CRC32


class SlotsImpl(override val size: Int) : Slots(size) {

    init {
//        repeat(size) { add(SlotImpl()) }
    }

    override fun fromBytes(data: ByteArray) {
        val crcGiven: Long = ValueConverters.UINT32.deserialize(data, data.size - 4)
        val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

        if (crcGiven != crcCalculated) {
            throw IllegalStateException("CRC doesn't match!")
        }

        val reader: ValueReader = ValueReaderImpl(data)
        // Read characteristic size
        reader.readUInt32()
        val slotCount: Int = reader.readUInt32().toInt()
        val slotNameSize: Short = reader.readUInt8()
        val frameTypeSize: Short = reader.readUInt8()
        val advertisingFormatSize: Int = reader.readUInt16()

        val config = Config(
            slotNameSize.toInt(),
            frameTypeSize.toInt(),
            advertisingFormatSize
        )

        Timber.d("Slot count - $slotCount")
        Timber.d("Slot name size - $slotNameSize")
        Timber.d("Slot frame type size - $frameTypeSize")
        Timber.d("Slot advertising format size - $advertisingFormatSize")

        for (i in 0 until slotCount) {
            val index = reader.currentIndex
            val slot = SlotImpl(reader, config)
            Timber.d("Slot $i (${slot.name} - ${slot.getType()}) - ${data.copyOfRange(index, reader.currentIndex).toCompactHex()}")
            add(slot)
        }

        Timber.d("Processed all slots")
    }

    override fun toBytes(): ByteArray {
        return mapIndexed { i, slot ->
            slot.toBytes().also { serializedData ->
                Timber.d("Slot $i (${slot.name} - ${slot.getType()}) - ${serializedData.toCompactHex()}")
            }
        }.flatten().let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
    }
}