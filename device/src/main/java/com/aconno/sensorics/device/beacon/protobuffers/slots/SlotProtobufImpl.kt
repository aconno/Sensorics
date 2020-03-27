package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.SlotsProtobufModel
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class SlotProtobufImpl(
    protobufModel : SlotsProtobufModel.Slot,
       private val config : Slots.Config,
       override var type_ : Type = Type.EMPTY,
       override val advertisingContent: MutableMap<String, String> = mutableMapOf()
) : Slot(type_,advertisingContent) {

    override var shownInUI: Boolean = false
    override var name: String = protobufModel.slotName
    override val readOnly: Boolean = protobufModel.readOnly
    override var active: Boolean = protobufModel.active
    override var packetCount: Int = protobufModel.packetCount
    override var txPower: Byte = protobufModel.txPower.toByte()
    override var dirty: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var advertisingMode: AdvertisingModeParameters.Mode =
        mapProtobufAdvertisingMode(protobufModel.advertisingMode)

    override lateinit var advertisingModeParameters: AdvertisingModeParameters

    init {
        advertisingModeParameters = TODO()
        type_ = mapProtobufSlotType(protobufModel.slotType)

        shownInUI = type_ != Type.EMPTY




    }

    private fun mapProtobufSlotType(slotType: SlotsProtobufModel.Slot.SlotType) : Type {
        try {
            return Type.valueOf(slotType.name)
        } catch (ex : IllegalArgumentException) {
            throw IllegalStateException("There is a mismatch between Type enum and protobuf SlotType enum. " +
                    "Protobuf enum value ${slotType.name} could not be mapped to any Type enum value.")
        }
    }


    private fun mapProtobufAdvertisingMode(protobufAdvMode : SlotsProtobufModel.Slot.AdvertisingMode) : AdvertisingModeParameters.Mode {
        try {
            return AdvertisingModeParameters.Mode.valueOf(protobufAdvMode.name)
        } catch (ex : IllegalArgumentException) {
            throw IllegalStateException("There is a mismatch between Mode enum and protobuf AdvertisingMode enum. " +
                    "Protobuf enum value ${protobufAdvMode.name} could not be mapped to any Mode enum value.")
        }
    }

    override fun toBytes(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toProtobufModel() : SlotsProtobufModel.Slot {
        TODO()
    }

}