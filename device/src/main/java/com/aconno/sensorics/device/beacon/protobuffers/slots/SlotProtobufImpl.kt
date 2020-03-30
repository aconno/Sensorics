package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.SlotsProtobufModel
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

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
    override var dirty: Boolean = false
        get() {
            val advContentByteArray = getAdvertisingDataConverter(type_).convertToProtobufModel(advertisingContent).toByteArray()
            return field || !(advContentByteArray?.contentEquals(rawAdvertisingContent) ?: true)
        }

    override var advertisingMode: AdvertisingModeParameters.Mode =
        getAdvertisingModeForProtobufAdvertisingMode(protobufModel.advertisingMode)

    override lateinit var advertisingModeParameters: AdvertisingModeParameters

    init {
        advertisingModeParameters =
            when(advertisingMode) {
                AdvertisingModeParameters.Mode.EVENT ->
                     EventAdvertisingModeParametersProtobufImpl(protobufModel.eventParameters)

                AdvertisingModeParameters.Mode.INTERVAL ->
                     IntervalAdvertisingModeParametersProtobufImpl(protobufModel.intervalParameters)
            }

        type_ = getSlotTypeForProtobufSlotType(protobufModel.slotType)

        shownInUI = type_ != Type.EMPTY

        advertisingContent.putAll(
            getAdvertisingDataConverter(type_).convertFromProtobufModel(protobufModel.advContent)
        )

        rawAdvertisingContent = protobufModel.advContent.toByteArray()

    }

    fun toProtobufModel() : SlotsProtobufModel.Slot {
        return SlotsProtobufModel.Slot.newBuilder()
            .setSlotName(name)
            .setReadOnly(readOnly)
            .setActive(active)
            .setPacketCount(packetCount)
            .setTxPower(txPower.toInt())
            .setAdvertisingMode(
                getProtobufAdvertisingModeForAdvertisingMode(advertisingMode)
            )
            .apply {
                when(this@SlotProtobufImpl.advertisingMode) {
                    AdvertisingModeParameters.Mode.EVENT ->
                        eventParameters = (advertisingModeParameters as EventAdvertisingModeParametersProtobufImpl)
                            .toProtobufModel()

                    AdvertisingModeParameters.Mode.INTERVAL ->
                        intervalParameters = (advertisingModeParameters as IntervalAdvertisingModeParametersProtobufImpl)
                            .toProtobufModel()
                }
            }
            .setSlotType(
                getProtobufSlotTypeForSlotType(type_)
            )
            .setAdvContent(
                getAdvertisingDataConverter(type_).convertToProtobufModel(advertisingContent)
            )
            .build()
    }

    private fun getAdvertisingDataConverter(type : Type) : AdvertisingContentProtobufConverter {
        return when(type) {
            Type.DEFAULT -> DefaultContentProtobufConverter()
            Type.UID -> UidContentProtobufConverter()
            Type.URL -> UrlContentProtobufConverter()
            Type.TLM -> TlmContentProtobufConverter()
            Type.I_BEACON -> IBeaconContentProtobufConverter()
            Type.DEVICE_INFO -> DeviceInfoContentProtobufConverter()
            Type.EMPTY -> EmptyContentProtobufConverter()
            Type.CUSTOM -> CustomContentProtobufConverter()

        }
    }

    //this method could be implemented in a way that it automatically maps values by their name,
    //but it is on purpose implemented using when statement so that any possible value mismatches
    //caused by some future changes to any of the enums can be caught in compile time
    private fun getSlotTypeForProtobufSlotType(slotType: SlotsProtobufModel.Slot.SlotType) : Type {
        return when(slotType) {
            SlotsProtobufModel.Slot.SlotType.CUSTOM -> Type.CUSTOM
            SlotsProtobufModel.Slot.SlotType.DEFAULT -> Type.DEFAULT
            SlotsProtobufModel.Slot.SlotType.DEVICE_INFO -> Type.DEVICE_INFO
            SlotsProtobufModel.Slot.SlotType.EMPTY -> Type.EMPTY
            SlotsProtobufModel.Slot.SlotType.I_BEACON -> Type.I_BEACON
            SlotsProtobufModel.Slot.SlotType.TLM -> Type.TLM
            SlotsProtobufModel.Slot.SlotType.UID -> Type.UID
            SlotsProtobufModel.Slot.SlotType.URL -> Type.URL
            SlotsProtobufModel.Slot.SlotType.UNRECOGNIZED -> throw IllegalStateException(
                "Slot type must not have value of UNRECOGNIZED"
            )
        }
    }

    private fun getProtobufSlotTypeForSlotType(slotType: Type) : SlotsProtobufModel.Slot.SlotType {
        return when(slotType) {
            Type.CUSTOM -> SlotsProtobufModel.Slot.SlotType.CUSTOM
            Type.DEFAULT -> SlotsProtobufModel.Slot.SlotType.DEFAULT
            Type.DEVICE_INFO -> SlotsProtobufModel.Slot.SlotType.DEVICE_INFO
            Type.EMPTY -> SlotsProtobufModel.Slot.SlotType.EMPTY
            Type.I_BEACON -> SlotsProtobufModel.Slot.SlotType.I_BEACON
            Type.TLM -> SlotsProtobufModel.Slot.SlotType.TLM
            Type.UID -> SlotsProtobufModel.Slot.SlotType.UID
            Type.URL -> SlotsProtobufModel.Slot.SlotType.URL
        }
    }


    private fun getAdvertisingModeForProtobufAdvertisingMode(protobufAdvMode : SlotsProtobufModel.Slot.AdvertisingMode) : AdvertisingModeParameters.Mode {
        return when(protobufAdvMode) {
            SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL -> AdvertisingModeParameters.Mode.INTERVAL
            SlotsProtobufModel.Slot.AdvertisingMode.EVENT -> AdvertisingModeParameters.Mode.EVENT
            SlotsProtobufModel.Slot.AdvertisingMode.UNRECOGNIZED -> throw IllegalStateException(
                "Advertising mode must not have value of UNRECOGNIZED"
            )
        }
    }

    private fun getProtobufAdvertisingModeForAdvertisingMode(advMode : AdvertisingModeParameters.Mode) : SlotsProtobufModel.Slot.AdvertisingMode {
        return when(advMode) {
            AdvertisingModeParameters.Mode.INTERVAL -> SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
            AdvertisingModeParameters.Mode.EVENT -> SlotsProtobufModel.Slot.AdvertisingMode.EVENT
        }
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
    }


    class IntervalAdvertisingModeParametersProtobufImpl(
        intervalModeParameters : SlotsProtobufModel.IntervalAdvertisingModeParameters) : AdvertisingModeParameters{

        override var interval: Long = intervalModeParameters.interval

        override var parameterId: Int = throw UnsupportedOperationException()
        override var sign: AdvertisingModeParameters.Sign = throw UnsupportedOperationException()
        override var thresholdInt: Long = throw UnsupportedOperationException()
        override var thresholdFloat: Float = throw UnsupportedOperationException()

        fun toProtobufModel() : SlotsProtobufModel.IntervalAdvertisingModeParameters {
            return SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                .setInterval(interval)
                .build()
        }
    }


    class EventAdvertisingModeParametersProtobufImpl(
            eventModeParameters : SlotsProtobufModel.EventAdvertisingModeParameters
    ) : AdvertisingModeParameters {

        override var parameterId: Int = eventModeParameters.parameterId

        override var sign: AdvertisingModeParameters.Sign = getSignForProtobufSign(
                eventModeParameters.sign
            )

        override var thresholdInt: Long = eventModeParameters.thresholdInt.toLong()

        override var thresholdFloat: Float = eventModeParameters.thresholdFloat

        override var interval: Long = throw UnsupportedOperationException()

        private fun getSignForProtobufSign(sign : SlotsProtobufModel.EventAdvertisingModeParameters.Sign) : AdvertisingModeParameters.Sign {
            return when(sign) {
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS -> AdvertisingModeParameters.Sign.LESS
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS_OR_EQUAL -> AdvertisingModeParameters.Sign.LESS_OR_EQUAL
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.EQUAL -> AdvertisingModeParameters.Sign.EQUAL
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER_OR_EQUAL -> AdvertisingModeParameters.Sign.GREATER_OR_EQUAL
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER -> AdvertisingModeParameters.Sign.GREATER
                SlotsProtobufModel.EventAdvertisingModeParameters.Sign.UNRECOGNIZED -> throw IllegalStateException("Value of sign must not be UNRECOGNIZED")
            }
        }

        private fun getProtobufSignForSign(sign : AdvertisingModeParameters.Sign) : SlotsProtobufModel.EventAdvertisingModeParameters.Sign {
            return when(sign) {
                AdvertisingModeParameters.Sign.LESS -> SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS
                AdvertisingModeParameters.Sign.LESS_OR_EQUAL -> SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS_OR_EQUAL
                AdvertisingModeParameters.Sign.EQUAL -> SlotsProtobufModel.EventAdvertisingModeParameters.Sign.EQUAL
                AdvertisingModeParameters.Sign.GREATER_OR_EQUAL -> SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER_OR_EQUAL
                AdvertisingModeParameters.Sign.GREATER -> SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER
            }
        }

        fun toProtobufModel() : SlotsProtobufModel.EventAdvertisingModeParameters {
            return SlotsProtobufModel.EventAdvertisingModeParameters.newBuilder()
                .setParameterId(parameterId)
                .setSign(getProtobufSignForSign(sign))
                .setThresholdInt(thresholdInt.toInt())
                .setThresholdFloat(thresholdFloat)
                .build()
        }

    }

}