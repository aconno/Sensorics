package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.SlotsProtobufModel
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.migrate.toCompactHex
import com.google.protobuf.ByteString
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.zip.CRC32

class SlotsProtobufImplTest {

    @Test
    fun testSimpleModelReading() {
        val slotsProtobufImpl = SlotsProtobufImpl(0)
        slotsProtobufImpl.fromBytes(buildTestReading(getSimpleProtobufModel()))

        assertThat(slotsProtobufImpl.config.NAME_SIZE, `is`(SLOT_NAME_SIZE))
        assertThat(slotsProtobufImpl.config.FRAME_TYPE_SIZE, `is`(FRAME_TYPE_SIZE))
        assertThat(slotsProtobufImpl.config.ADV_FORMAT_SIZE, `is`(ADVERTISING_FORMAT_SIZE))
    }

    @Test
    fun testFullModelReading() {
        val slotsProtobufImpl = SlotsProtobufImpl(6)
        slotsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        assertThat(slotsProtobufImpl.config.NAME_SIZE, `is`(SLOT_NAME_SIZE))
        assertThat(slotsProtobufImpl.config.FRAME_TYPE_SIZE, `is`(FRAME_TYPE_SIZE))
        assertThat(slotsProtobufImpl.config.ADV_FORMAT_SIZE, `is`(ADVERTISING_FORMAT_SIZE))

        assertThat(slotsProtobufImpl.size, `is`(6))



        val slot1 = slotsProtobufImpl[0]

        assertThat(slot1.advertisingMode, `is`(SLOT1_ADV_MODE))
        assertThat(slot1.advertisingModeParameters.interval, `is`(SLOT1_INTERVAL))
        assertThat(slot1.readOnly, `is`(SLOT1_READ_ONLY))
        assertThat(slot1.active, `is`(SLOT1_ACTIVE))
        assertThat(slot1.packetCount, `is`(SLOT1_PACKET_COUNT))
        assertThat(slot1.name, `is`(SLOT1_NAME))
        assertThat(slot1.getType(), `is`(SLOT1_TYPE))
        assertThat(slot1.txPower, `is`(SLOT1_TX_POWER.toByte()))
        assertThat(slot1.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA], `is`(
            SLOT1_DEFAULT_DATA.toCompactHex()))



        val slot2 = slotsProtobufImpl[1]

        assertThat(slot2.advertisingMode, `is`(SLOT2_ADV_MODE))
        assertThat(slot2.advertisingModeParameters.parameterId, `is`(SLOT2_PARAM_ID))
        assertThat(slot2.advertisingModeParameters.sign, `is`(SLOT2_SIGN))
        assertThat(slot2.advertisingModeParameters.thresholdInt, `is`(SLOT2_THRESHOLD_INT.toLong()))
        assertThat(slot2.readOnly, `is`(SLOT2_READ_ONLY))
        assertThat(slot2.active, `is`(SLOT2_ACTIVE))
        assertThat(slot2.packetCount, `is`(SLOT2_PACKET_COUNT))
        assertThat(slot2.name, `is`(SLOT2_NAME))
        assertThat(slot2.getType(), `is`(SLOT2_TYPE))
        assertThat(slot2.txPower, `is`(SLOT2_TX_POWER.toByte()))
        assertThat(slot2.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID], `is`(
            SLOT2_NAMESPACE_ID.toCompactHex()))
        assertThat(slot2.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID], `is`(
            SLOT2_INSTANCE_ID.toCompactHex()))



        val slot3 = slotsProtobufImpl[2]

        assertThat(slot3.advertisingMode, `is`(SLOT3_ADV_MODE))
        assertThat(slot3.advertisingModeParameters.parameterId, `is`(SLOT3_PARAM_ID))
        assertThat(slot3.advertisingModeParameters.sign, `is`(SLOT3_SIGN))
        assertThat(slot3.advertisingModeParameters.thresholdFloat, `is`(SLOT3_THRESHOLD_FLOAT))
        assertThat(slot3.readOnly, `is`(SLOT3_READ_ONLY))
        assertThat(slot3.active, `is`(SLOT3_ACTIVE))
        assertThat(slot3.packetCount, `is`(SLOT3_PACKET_COUNT))
        assertThat(slot3.name, `is`(SLOT3_NAME))
        assertThat(slot3.getType(), `is`(SLOT3_TYPE))
        assertThat(slot3.txPower, `is`(SLOT3_TX_POWER.toByte()))
        assertThat(slot3.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_URL_URL], `is`(
            SLOT3_URL_WITH_PREFIX))



        val slot4 = slotsProtobufImpl[3]

        assertThat(slot4.advertisingMode, `is`(SLOT4_ADV_MODE))
        assertThat(slot4.advertisingModeParameters.interval, `is`(SLOT4_INTERVAL))
        assertThat(slot4.readOnly, `is`(SLOT4_READ_ONLY))
        assertThat(slot4.active, `is`(SLOT4_ACTIVE))
        assertThat(slot4.packetCount, `is`(SLOT4_PACKET_COUNT))
        assertThat(slot4.name, `is`(SLOT4_NAME))
        assertThat(slot4.getType(), `is`(SLOT4_TYPE))
        assertThat(slot4.txPower, `is`(SLOT4_TX_POWER.toByte()))




        val slot5 = slotsProtobufImpl[4]

        assertThat(slot5.advertisingMode, `is`(SLOT5_ADV_MODE))
        assertThat(slot5.advertisingModeParameters.interval, `is`(SLOT5_INTERVAL))
        assertThat(slot5.readOnly, `is`(SLOT5_READ_ONLY))
        assertThat(slot5.active, `is`(SLOT5_ACTIVE))
        assertThat(slot5.packetCount, `is`(SLOT5_PACKET_COUNT))
        assertThat(slot5.name, `is`(SLOT5_NAME))
        assertThat(slot5.getType(), `is`(SLOT5_TYPE))
        assertThat(slot5.txPower, `is`(SLOT5_TX_POWER.toByte()))
        assertThat(slot5.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID], `is`(
            SLOT5_UUID))
        assertThat(slot5.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR], `is`(
            SLOT5_MAJOR.toString()))
        assertThat(slot5.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR], `is`(
            SLOT5_MINOR.toString()))




        val slot6 = slotsProtobufImpl[5]

        assertThat(slot6.advertisingMode, `is`(SLOT6_ADV_MODE))
        assertThat(slot6.advertisingModeParameters.interval, `is`(SLOT6_INTERVAL))
        assertThat(slot6.readOnly, `is`(SLOT6_READ_ONLY))
        assertThat(slot6.active, `is`(SLOT6_ACTIVE))
        assertThat(slot6.packetCount, `is`(SLOT6_PACKET_COUNT))
        assertThat(slot6.name, `is`(SLOT6_NAME))
        assertThat(slot6.getType(), `is`(SLOT6_TYPE))
        assertThat(slot6.txPower, `is`(SLOT6_TX_POWER.toByte()))
        assertThat(slot6.advertisingContent[Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM], `is`(
            SLOT6_CUSTOM_CONTENT.toCompactHex()))
    }

    @Test(expected = IllegalStateException::class)
    fun testCorruptedReading() {
        val slotsProtobufImpl = SlotsProtobufImpl(6)

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[10] = (bytes[10] + 5).toByte() //changing some byte to simulate corrupted reading

        slotsProtobufImpl.fromBytes(bytes)
    }


    @Test
    fun testWritingUnchangedContent() {
        val slotsProtobufImpl = SlotsProtobufImpl(6)
        slotsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        val bytes = slotsProtobufImpl.toBytes()
        val protobufModel = SlotsProtobufModel.Slots.parseFrom(
            bytes.sliceArray(IntRange(0,bytes.size - 5))
        )


        val slot1 = protobufModel.getSlot(0)

        assertThat(slot1.slotName, `is`(SLOT1_NAME))
        assertThat(slot1.packetCount, `is`(SLOT1_PACKET_COUNT))
        assertThat(slot1.active, `is`(SLOT1_ACTIVE))
        assertThat(slot1.readOnly, `is`(SLOT1_READ_ONLY))
        assertThat(slot1.txPower, `is`(SLOT1_TX_POWER))
        assertThat(slot1.advertisingMode, `is`(SLOT1_ADV_MODE_PROTOBUF))
        assertThat(slot1.slotType, `is`(SLOT1_TYPE_PROTOBUF))
        assertThat(slot1.intervalParameters.interval, `is`(SLOT1_INTERVAL))
        assertThat(slot1.advContent.defaultAdvContentTypeParameters.defaultData.toByteArray(), `is`(
            SLOT1_DEFAULT_DATA))


        val slot2 = protobufModel.getSlot(1)

        assertThat(slot2.slotName, `is`(SLOT2_NAME))
        assertThat(slot2.packetCount, `is`(SLOT2_PACKET_COUNT))
        assertThat(slot2.active, `is`(SLOT2_ACTIVE))
        assertThat(slot2.txPower, `is`(SLOT2_TX_POWER))
        assertThat(slot2.advertisingMode, `is`(SLOT2_ADV_MODE_PROTOBUF))
        assertThat(slot2.slotType, `is`(SLOT2_TYPE_PROTOBUF))
        assertThat(slot2.eventParameters.parameterId, `is`(SLOT2_PARAM_ID))
        assertThat(slot2.eventParameters.sign, `is`(SLOT2_SIGN_PROTOBUF))
        assertThat(slot2.eventParameters.thresholdInt, `is`(SLOT2_THRESHOLD_INT))
        assertThat(slot2.advContent.uidAdvContentTypeParameters.namespaceId.toByteArray(), `is`(
            SLOT2_NAMESPACE_ID))
        assertThat(slot2.advContent.uidAdvContentTypeParameters.instanceId.toByteArray(), `is`(
            SLOT2_INSTANCE_ID))


        val slot3 = protobufModel.getSlot(2)

        assertThat(slot3.slotName, `is`(SLOT3_NAME))
        assertThat(slot3.packetCount, `is`(SLOT3_PACKET_COUNT))
        assertThat(slot3.active, `is`(SLOT3_ACTIVE))
        assertThat(slot3.txPower, `is`(SLOT3_TX_POWER))
        assertThat(slot3.advertisingMode, `is`(SLOT3_ADV_MODE_PROTOBUF))
        assertThat(slot3.slotType, `is`(SLOT3_TYPE_PROTOBUF))
        assertThat(slot3.eventParameters.parameterId, `is`(SLOT3_PARAM_ID))
        assertThat(slot3.eventParameters.sign, `is`(SLOT3_SIGN_PROTOBUF))
        assertThat(slot3.eventParameters.thresholdFloat, `is`(SLOT3_THRESHOLD_FLOAT))
        assertThat(slot3.advContent.urlAdvContentTypeParameters.url, `is`(
            SLOT3_URL))
        assertThat(slot3.advContent.urlAdvContentTypeParameters.urlPrefix, `is`(
            SLOT3_URL_PREFIX))


        val slot4 = protobufModel.getSlot(3)

        assertThat(slot4.slotName, `is`(SLOT4_NAME))
        assertThat(slot4.packetCount, `is`(SLOT4_PACKET_COUNT))
        assertThat(slot4.active, `is`(SLOT4_ACTIVE))
        assertThat(slot4.txPower, `is`(SLOT4_TX_POWER))
        assertThat(slot4.advertisingMode, `is`(SLOT4_ADV_MODE_PROTOBUF))
        assertThat(slot4.slotType, `is`(SLOT4_TYPE_PROTOBUF))
        assertThat(slot4.intervalParameters.interval, `is`(SLOT4_INTERVAL))


        val slot5 = protobufModel.getSlot(4)

        assertThat(slot5.slotName, `is`(SLOT5_NAME))
        assertThat(slot5.packetCount, `is`(SLOT5_PACKET_COUNT))
        assertThat(slot5.active, `is`(SLOT5_ACTIVE))
        assertThat(slot5.txPower, `is`(SLOT5_TX_POWER))
        assertThat(slot5.advertisingMode, `is`(SLOT5_ADV_MODE_PROTOBUF))
        assertThat(slot5.slotType, `is`(SLOT5_TYPE_PROTOBUF))
        assertThat(slot5.intervalParameters.interval, `is`(SLOT5_INTERVAL))
        assertThat(slot5.advContent.ibeaconAdvContentTypeParameters.uuid, `is`(
            SLOT5_UUID))
        assertThat(slot5.advContent.ibeaconAdvContentTypeParameters.major, `is`(
            SLOT5_MAJOR))
        assertThat(slot5.advContent.ibeaconAdvContentTypeParameters.minor, `is`(
            SLOT5_MINOR))


        val slot6 = protobufModel.getSlot(5)

        assertThat(slot6.slotName, `is`(SLOT6_NAME))
        assertThat(slot6.packetCount, `is`(SLOT6_PACKET_COUNT))
        assertThat(slot6.active, `is`(SLOT6_ACTIVE))
        assertThat(slot6.txPower, `is`(SLOT6_TX_POWER))
        assertThat(slot6.advertisingMode, `is`(SLOT6_ADV_MODE_PROTOBUF))
        assertThat(slot6.slotType, `is`(SLOT6_TYPE_PROTOBUF))
        assertThat(slot6.intervalParameters.interval, `is`(SLOT6_INTERVAL))
        assertThat(slot6.advContent.customAdvContentTypeParameters.customContent.toByteArray(), `is`(
            SLOT6_CUSTOM_CONTENT))
    }



    @Test
    fun testWritingEditedContent() {
        val slotsProtobufImpl = SlotsProtobufImpl(6)
        slotsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        val slot1NewName = "slot1NewName"
        val slot1NewPacketCount = 12
        val slot1NewTxPower = 20
        val slot1NewAdvMode = Slot.AdvertisingModeParameters.Mode.EVENT
        val slot1NewAdvModeProto = SlotsProtobufModel.Slot.AdvertisingMode.EVENT
        val slot1ParamId = 343
        val slot1SignProto = SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS
        val slot1ThresholdInt = 300
        val slot1Uuid = "123e4567-aa9b-16d3-a456-b28655440000"
        val slot1Major = 23
        val slot1Minor = 21
        val slot1Type = Slot.Type.I_BEACON
        val slot1TypeProto = SlotsProtobufModel.Slot.SlotType.I_BEACON
        slotsProtobufImpl[0].apply {
            name = slot1NewName
            packetCount = slot1NewPacketCount
            txPower = slot1NewTxPower.toByte()
            advertisingMode = slot1NewAdvMode
            setType(slot1Type)
            advertisingModeParameters = SlotProtobufImpl.EventAdvertisingModeParametersProtobufImpl(
                SlotsProtobufModel.EventAdvertisingModeParameters.newBuilder()
                    .setThresholdInt(slot1ThresholdInt)
                    .setSign(slot1SignProto)
                    .setParameterId(slot1ParamId)
                    .build()
            )
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID] = slot1Uuid
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR] = slot1Major.toString()
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR] = slot1Minor.toString()
        }



        val slot2NewName = "slot2NewName"
        val slot2NewPacketCount = 18
        val slot2NewTxPower = 15
        val slot2NewAdvMode = Slot.AdvertisingModeParameters.Mode.INTERVAL
        val slot2NewAdvModeProto = SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
        val slot2Interval = 2500L
        val slot2Url = "http://random-page.com"
        val slot2UrlWithoutPrefix = "random-page.com"
        val slot2UrlPrefix = SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTP

        val slot2Type = Slot.Type.URL
        val slot2TypeProto = SlotsProtobufModel.Slot.SlotType.URL
        slotsProtobufImpl[1].apply {
            name = slot2NewName
            packetCount = slot2NewPacketCount
            txPower = slot2NewTxPower.toByte()
            advertisingMode = slot2NewAdvMode
            setType(slot2Type)
            advertisingModeParameters = SlotProtobufImpl.IntervalAdvertisingModeParametersProtobufImpl(
                SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                    .setInterval(slot2Interval)
                    .build()
            )
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_URL_URL] = slot2Url
        }




        val slot3NewName = "slot3NewName"
        val slot3NewPacketCount = 8
        val slot3NewTxPower = 5
        val slot3ParamId = 111
        val slot3SignProto = SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER_OR_EQUAL
        val slot3Sign = Slot.AdvertisingModeParameters.Sign.GREATER_OR_EQUAL
        val slot3ThresholdInt = 222L
        val slot3Type = Slot.Type.EMPTY
        val slot3TypeProto = SlotsProtobufModel.Slot.SlotType.EMPTY
        slotsProtobufImpl[2].apply {
            name = slot3NewName
            packetCount = slot3NewPacketCount
            txPower = slot3NewTxPower.toByte()
            setType(slot3Type)
            advertisingModeParameters.parameterId = slot3ParamId
            advertisingModeParameters.thresholdInt = slot3ThresholdInt
            advertisingModeParameters.sign = slot3Sign
        }



        val slot4Type = Slot.Type.UID
        val slot4TypeProto = SlotsProtobufModel.Slot.SlotType.UID
        val slot4NamespaceId = byteArrayOf(1,5,2,56,23)
        val slot4InstanceId = byteArrayOf(54,23,44,1,4,5,23)
        slotsProtobufImpl[3].apply {
            setType(slot4Type)
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID] = slot4NamespaceId.toCompactHex()
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID] = slot4InstanceId.toCompactHex()
        }



        val slot5Type = Slot.Type.CUSTOM
        val slot5TypeProto = SlotsProtobufModel.Slot.SlotType.CUSTOM
        val slot5CustomContent = byteArrayOf(12,52,22,56,123)
        slotsProtobufImpl[4].apply {
            setType(slot5Type)
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM] = slot5CustomContent.toCompactHex()
        }


        val slot6Type = Slot.Type.DEFAULT
        val slot6TypeProto = SlotsProtobufModel.Slot.SlotType.DEFAULT
        val slot6DefaultContent = byteArrayOf(82,22,32,56,113)
        slotsProtobufImpl[5].apply {
            setType(slot6Type)
            advertisingContent[Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA] = slot6DefaultContent.toCompactHex()
        }



        val bytes = slotsProtobufImpl.toBytes()
        val protobufModel = SlotsProtobufModel.Slots.parseFrom(
            bytes.sliceArray(IntRange(0,bytes.size - 5))
        )


        val slot1 = protobufModel.getSlot(0)

        assertThat(slot1.slotName, `is`(slot1NewName))
        assertThat(slot1.packetCount, `is`(slot1NewPacketCount))
        assertThat(slot1.txPower, `is`(slot1NewTxPower))
        assertThat(slot1.advertisingMode, `is`(slot1NewAdvModeProto))
        assertThat(slot1.slotType, `is`(slot1TypeProto))
        assertThat(slot1.eventParameters.parameterId, `is`(slot1ParamId))
        assertThat(slot1.eventParameters.sign, `is`(slot1SignProto))
        assertThat(slot1.eventParameters.thresholdInt, `is`(slot1ThresholdInt))
        assertThat(slot1.advContent.ibeaconAdvContentTypeParameters.uuid, `is`(slot1Uuid))
        assertThat(slot1.advContent.ibeaconAdvContentTypeParameters.major, `is`(slot1Major))
        assertThat(slot1.advContent.ibeaconAdvContentTypeParameters.minor, `is`(slot1Minor))


        val slot2 = protobufModel.getSlot(1)

        assertThat(slot2.slotName, `is`(slot2NewName))
        assertThat(slot2.packetCount, `is`(slot2NewPacketCount))
        assertThat(slot2.txPower, `is`(slot2NewTxPower))
        assertThat(slot2.advertisingMode, `is`(slot2NewAdvModeProto))
        assertThat(slot2.slotType, `is`(slot2TypeProto))
        assertThat(slot2.intervalParameters.interval, `is`(slot2Interval))
        assertThat(slot2.advContent.urlAdvContentTypeParameters.url, `is`(slot2UrlWithoutPrefix))
        assertThat(slot2.advContent.urlAdvContentTypeParameters.urlPrefix, `is`(slot2UrlPrefix))


        val slot3 = protobufModel.getSlot(2)

        assertThat(slot3.slotName, `is`(slot3NewName))
        assertThat(slot3.packetCount, `is`(slot3NewPacketCount))
        assertThat(slot3.txPower, `is`(slot3NewTxPower))
        assertThat(slot3.advertisingMode, `is`(SLOT3_ADV_MODE_PROTOBUF))
        assertThat(slot3.slotType, `is`(slot3TypeProto))
        assertThat(slot3.eventParameters.parameterId, `is`(slot3ParamId))
        assertThat(slot3.eventParameters.sign, `is`(slot3SignProto))
        assertThat(slot3.eventParameters.thresholdInt, `is`(slot3ThresholdInt.toInt()))


        val slot4 = protobufModel.getSlot(3)

        assertThat(slot4.slotName, `is`(SLOT4_NAME))
        assertThat(slot4.packetCount, `is`(SLOT4_PACKET_COUNT))
        assertThat(slot4.txPower, `is`(SLOT4_TX_POWER))
        assertThat(slot4.advertisingMode, `is`(SLOT4_ADV_MODE_PROTOBUF))
        assertThat(slot4.slotType, `is`(slot4TypeProto))
        assertThat(slot4.advContent.uidAdvContentTypeParameters.namespaceId.toByteArray(), `is`(slot4NamespaceId))
        assertThat(slot4.advContent.uidAdvContentTypeParameters.instanceId.toByteArray(), `is`(slot4InstanceId))


        val slot5 = protobufModel.getSlot(4)

        assertThat(slot5.slotName, `is`(SLOT5_NAME))
        assertThat(slot5.packetCount, `is`(SLOT5_PACKET_COUNT))
        assertThat(slot5.txPower, `is`(SLOT5_TX_POWER))
        assertThat(slot5.advertisingMode, `is`(SLOT5_ADV_MODE_PROTOBUF))
        assertThat(slot5.slotType, `is`(slot5TypeProto))
        assertThat(slot5.advContent.customAdvContentTypeParameters.customContent.toByteArray(), `is`(slot5CustomContent))


        val slot6 = protobufModel.getSlot(5)

        assertThat(slot6.slotName, `is`(SLOT6_NAME))
        assertThat(slot6.packetCount, `is`(SLOT6_PACKET_COUNT))
        assertThat(slot6.txPower, `is`(SLOT6_TX_POWER))
        assertThat(slot6.advertisingMode, `is`(SLOT6_ADV_MODE_PROTOBUF))
        assertThat(slot6.slotType, `is`(slot6TypeProto))
        assertThat(slot6.advContent.defaultAdvContentTypeParameters.defaultData.toByteArray(), `is`(slot6DefaultContent))
    }



    private fun getSimpleProtobufModel(): SlotsProtobufModel.Slots {
        return SlotsProtobufModel.Slots.newBuilder()
                    .setSlotNameSize(SLOT_NAME_SIZE)
                    .setFrameTypeSize(FRAME_TYPE_SIZE)
                    .setAdvertisingFormatSize(ADVERTISING_FORMAT_SIZE)
            .build()
    }

    private fun getFullProtobufModel(): SlotsProtobufModel.Slots {

        val slot1 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT1_ADV_MODE_PROTOBUF)
            .setIntervalParameters(
                SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                    .setInterval(SLOT1_INTERVAL)
            )
            .setReadOnly(SLOT1_READ_ONLY)
            .setActive(SLOT1_ACTIVE)
            .setPacketCount(SLOT1_PACKET_COUNT)
            .setSlotName(SLOT1_NAME)
            .setSlotType(SLOT1_TYPE_PROTOBUF)
            .setTxPower(SLOT1_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setDefaultAdvContentTypeParameters(
                        SlotsProtobufModel.DefaultAdvContentTypeParameters.newBuilder()
                            .setDefaultData(ByteString.copyFrom(SLOT1_DEFAULT_DATA))
                    )
            ).build()

        val slot2 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT2_ADV_MODE_PROTOBUF)
            .setEventParameters(
                SlotsProtobufModel.EventAdvertisingModeParameters.newBuilder()
                    .setSign(SLOT2_SIGN_PROTOBUF)
                    .setParameterId(SLOT2_PARAM_ID)
                    .setThresholdInt(SLOT2_THRESHOLD_INT)
            )
            .setReadOnly(SLOT2_READ_ONLY)
            .setActive(SLOT2_ACTIVE)
            .setPacketCount(SLOT2_PACKET_COUNT)
            .setSlotName(SLOT2_NAME)
            .setSlotType(SLOT2_TYPE_PROTOBUF)
            .setTxPower(SLOT2_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setUidAdvContentTypeParameters(
                        SlotsProtobufModel.UidAdvContentTypeParameters.newBuilder()
                            .setNamespaceId(ByteString.copyFrom(SLOT2_NAMESPACE_ID))
                            .setInstanceId(ByteString.copyFrom(SLOT2_INSTANCE_ID))
                    )
            ).build()


        val slot3 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT3_ADV_MODE_PROTOBUF)
            .setEventParameters(
                SlotsProtobufModel.EventAdvertisingModeParameters.newBuilder()
                    .setSign(SLOT3_SIGN_PROTOBUF)
                    .setParameterId(SLOT3_PARAM_ID)
                    .setThresholdFloat(SLOT3_THRESHOLD_FLOAT)
            )
            .setReadOnly(SLOT3_READ_ONLY)
            .setActive(SLOT3_ACTIVE)
            .setPacketCount(SLOT3_PACKET_COUNT)
            .setSlotName(SLOT3_NAME)
            .setSlotType(SLOT3_TYPE_PROTOBUF)
            .setTxPower(SLOT3_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setUrlAdvContentTypeParameters(
                        SlotsProtobufModel.UrlAdvContentTypeParameters.newBuilder()
                            .setUrlPrefix(SLOT3_URL_PREFIX)
                            .setUrl(SLOT3_URL)
                    )
            ).build()

        val slot4 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT4_ADV_MODE_PROTOBUF)
            .setIntervalParameters(
                SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                    .setInterval(SLOT4_INTERVAL)
            )
            .setReadOnly(SLOT4_READ_ONLY)
            .setActive(SLOT4_ACTIVE)
            .setPacketCount(SLOT4_PACKET_COUNT)
            .setSlotName(SLOT4_NAME)
            .setSlotType(SLOT4_TYPE_PROTOBUF)
            .setTxPower(SLOT4_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setTlmAdvContentTypeParameters(
                        SlotsProtobufModel.TlmAdvContentTypeParameters.newBuilder()
                            .setContent(ByteString.copyFrom(SLOT4_CONTENT))
                    )
            ).build()

        val slot5 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT5_ADV_MODE_PROTOBUF)
            .setIntervalParameters(
                SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                    .setInterval(SLOT5_INTERVAL)
            )
            .setReadOnly(SLOT5_READ_ONLY)
            .setActive(SLOT5_ACTIVE)
            .setPacketCount(SLOT5_PACKET_COUNT)
            .setSlotName(SLOT5_NAME)
            .setSlotType(SLOT5_TYPE_PROTOBUF)
            .setTxPower(SLOT5_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setIbeaconAdvContentTypeParameters(
                        SlotsProtobufModel.IBeaconAdvContentTypeParameters.newBuilder()
                            .setUuid(SLOT5_UUID)
                            .setMajor(SLOT5_MAJOR)
                            .setMinor(SLOT5_MINOR)
                    )
            ).build()

        val slot6 = SlotsProtobufModel.Slot.newBuilder()
            .setAdvertisingMode(SLOT6_ADV_MODE_PROTOBUF)
            .setIntervalParameters(
                SlotsProtobufModel.IntervalAdvertisingModeParameters.newBuilder()
                    .setInterval(SLOT6_INTERVAL)
            )
            .setReadOnly(SLOT6_READ_ONLY)
            .setActive(SLOT6_ACTIVE)
            .setPacketCount(SLOT6_PACKET_COUNT)
            .setSlotName(SLOT6_NAME)
            .setSlotType(SLOT6_TYPE_PROTOBUF)
            .setTxPower(SLOT6_TX_POWER)
            .setAdvContent(
                SlotsProtobufModel.AdvertisingContent.newBuilder()
                    .setCustomAdvContentTypeParameters(
                        SlotsProtobufModel.CustomAdvContentTypeParameters.newBuilder()
                            .setCustomContent(ByteString.copyFrom(SLOT6_CUSTOM_CONTENT))
                    )
            ).build()

        return SlotsProtobufModel.Slots.newBuilder()
            .setSlotNameSize(SLOT_NAME_SIZE)
            .setFrameTypeSize(FRAME_TYPE_SIZE)
            .setAdvertisingFormatSize(ADVERTISING_FORMAT_SIZE)
            .addSlot(slot1)
            .addSlot(slot2)
            .addSlot(slot3)
            .addSlot(slot4)
            .addSlot(slot5)
            .addSlot(slot6)
            .build()
    }

    private fun buildTestReading(protobufModel: SlotsProtobufModel.Slots) : ByteArray  {
        val protobufModelBytes = protobufModel.toByteArray()
        val crc = CRC32().getValueForUpdate(protobufModelBytes)

        return protobufModelBytes + ValueConverters.UINT32.serialize(crc)
    }


    companion object {
        const val SLOT_NAME_SIZE = 100
        const val FRAME_TYPE_SIZE = 200
        const val ADVERTISING_FORMAT_SIZE = 300

        val SLOT1_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
        val SLOT1_ADV_MODE = Slot.AdvertisingModeParameters.Mode.INTERVAL
        const val SLOT1_INTERVAL = 555L
        const val SLOT1_READ_ONLY = false
        const val SLOT1_ACTIVE = true
        const val SLOT1_PACKET_COUNT = 10
        const val SLOT1_NAME = "slot1"
        val SLOT1_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.DEFAULT
        val SLOT1_TYPE = Slot.Type.DEFAULT
        const val SLOT1_TX_POWER = 4
        val SLOT1_DEFAULT_DATA = byteArrayOf(5,8,2,7,54,65,45,56,43)


        val SLOT2_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.EVENT
        val SLOT2_ADV_MODE = Slot.AdvertisingModeParameters.Mode.EVENT
        val SLOT2_SIGN_PROTOBUF = SlotsProtobufModel.EventAdvertisingModeParameters.Sign.GREATER_OR_EQUAL
        val SLOT2_SIGN = Slot.AdvertisingModeParameters.Sign.GREATER_OR_EQUAL
        const val SLOT2_PARAM_ID = 25
        const val SLOT2_THRESHOLD_INT = 254
        const val SLOT2_READ_ONLY = true
        const val SLOT2_ACTIVE = false
        const val SLOT2_PACKET_COUNT = 5
        const val SLOT2_NAME = "slot2"
        val SLOT2_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.UID
        val SLOT2_TYPE = Slot.Type.UID
        const val SLOT2_TX_POWER = -4
        val SLOT2_NAMESPACE_ID = byteArrayOf(12,14,45,67,23,54)
        val SLOT2_INSTANCE_ID = byteArrayOf(78,53,23,76,89,45)

        val SLOT3_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.EVENT
        val SLOT3_ADV_MODE = Slot.AdvertisingModeParameters.Mode.EVENT
        val SLOT3_SIGN_PROTOBUF = SlotsProtobufModel.EventAdvertisingModeParameters.Sign.LESS
        val SLOT3_SIGN = Slot.AdvertisingModeParameters.Sign.LESS
        const val SLOT3_PARAM_ID = 125
        const val SLOT3_THRESHOLD_FLOAT = 123.34f
        const val SLOT3_READ_ONLY = false
        const val SLOT3_ACTIVE = false
        const val SLOT3_PACKET_COUNT = 15
        const val SLOT3_NAME = "slot3"
        val SLOT3_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.URL
        val SLOT3_TYPE = Slot.Type.URL
        const val SLOT3_TX_POWER = 8
        val SLOT3_URL_PREFIX = SlotsProtobufModel.UrlAdvContentTypeParameters.UrlPrefix.HTTPS_WWW
        const val SLOT3_URL = "aconno.de"
        const val SLOT3_URL_WITH_PREFIX = "https://www.aconno.de"


        val SLOT4_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
        val SLOT4_ADV_MODE = Slot.AdvertisingModeParameters.Mode.INTERVAL
        const val SLOT4_INTERVAL = 1255L
        const val SLOT4_READ_ONLY = true
        const val SLOT4_ACTIVE = false
        const val SLOT4_PACKET_COUNT = 20
        const val SLOT4_NAME = "slot4"
        val SLOT4_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.TLM
        val SLOT4_TYPE = Slot.Type.TLM
        const val SLOT4_TX_POWER = 16
        val SLOT4_CONTENT = byteArrayOf(23,54,23,21,56,33,22)

        val SLOT5_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
        val SLOT5_ADV_MODE = Slot.AdvertisingModeParameters.Mode.INTERVAL
        const val SLOT5_INTERVAL = 333L
        const val SLOT5_READ_ONLY = true
        const val SLOT5_ACTIVE = false
        const val SLOT5_PACKET_COUNT = 15
        const val SLOT5_NAME = "slot5"
        val SLOT5_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.I_BEACON
        val SLOT5_TYPE = Slot.Type.I_BEACON
        const val SLOT5_TX_POWER = 8
        const val SLOT5_UUID = "123e4567-e89b-12d3-a456-426655440000"
        const val SLOT5_MAJOR = 5
        const val SLOT5_MINOR = 2

        val SLOT6_ADV_MODE_PROTOBUF = SlotsProtobufModel.Slot.AdvertisingMode.INTERVAL
        val SLOT6_ADV_MODE = Slot.AdvertisingModeParameters.Mode.INTERVAL
        const val SLOT6_INTERVAL = 543L
        const val SLOT6_READ_ONLY = false
        const val SLOT6_ACTIVE = true
        const val SLOT6_PACKET_COUNT = 8
        const val SLOT6_NAME = "slot6"
        val SLOT6_TYPE_PROTOBUF = SlotsProtobufModel.Slot.SlotType.CUSTOM
        val SLOT6_TYPE = Slot.Type.CUSTOM
        const val SLOT6_TX_POWER = 4
        val SLOT6_CUSTOM_CONTENT = byteArrayOf(65,23,43,78,65,31,35,67)
    }

}