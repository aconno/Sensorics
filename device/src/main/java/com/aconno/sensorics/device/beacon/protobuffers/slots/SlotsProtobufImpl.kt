package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.SlotsProtobufModel
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import java.util.zip.CRC32

class SlotsProtobufImpl(override val size: Int) : Slots(size) {
    override lateinit var config: Config
    private lateinit var slotsProtobufModel : SlotsProtobufModel.Slots

    override fun fromBytes(data: ByteArray) {
        val crcGiven: Long = UINT32.deserialize(data, data.size - 4)
        val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

        if (crcGiven != crcCalculated) {
            throw IllegalStateException("CRC doesn't match!")
        }

        //parse all bytes except last 4 bytes that represent CRC
        slotsProtobufModel = SlotsProtobufModel.Slots.parseFrom(data.sliceArray(
            IntRange(0,data.size - 5)
        ))

        config = Config(
            slotsProtobufModel.slotNameSize,
            slotsProtobufModel.frameTypeSize,
            slotsProtobufModel.advertisingFormatSize
        )

        for(slot in slotsProtobufModel.slotList) {
            add(SlotProtobufImpl(slot,config))
        }

    }

    override fun toBytes(): ByteArray {
        updateProtobufModel()
        return slotsProtobufModel.toByteArray().let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
    }

    private fun updateProtobufModel() {
        slotsProtobufModel = SlotsProtobufModel.Slots.newBuilder()
            .addAllSlot(map { slot  -> (slot as SlotProtobufImpl).toProtobufModel() })
            .build()
    }


}