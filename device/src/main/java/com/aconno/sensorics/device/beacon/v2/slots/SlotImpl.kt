package com.aconno.sensorics.device.beacon.v2.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slot.AdvertisingModeParameters.Mode.EVENT
import com.aconno.sensorics.device.beacon.Slot.AdvertisingModeParameters.Mode.INTERVAL
import com.aconno.sensorics.device.beacon.Slots.Config
import com.aconno.sensorics.domain.migrate.*
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.ASCII_STRING
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT8
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT16
import java.nio.ByteOrder


class SlotImpl(
    reader: ValueReader,
    private val config: Config,
    override var type_: Type = Type.EMPTY,
    override val advertisingContent: MutableMap<String, String> = mutableMapOf()
) : Slot(type_, advertisingContent) {

    private var flags: Int

    override var shownInUI: Boolean = false

    override val readOnly: Boolean
        get() = flags extractFlag 0

    override var advertisingMode: AdvertisingModeParameters.Mode
        get() = when (flags extractFlag 1) {
            false -> INTERVAL
            true -> EVENT
        }
        set(value) {
            flags = when (value) {
                INTERVAL -> flags clearFlag 1
                EVENT -> flags setFlag 1
            }
        }

    override var active: Boolean
        get() = flags extractFlag 2
        set(value) {
            flags = when (value) {
                true -> flags setFlag 2
                false -> flags clearFlag 2
            }
        }

    override var packetCount: Int = 0

    override var txPower: Byte = 0

    override lateinit var name: String

    override lateinit var advertisingModeParameters: AdvertisingModeParameters

    constructor(data: ByteArray, config: Config) : this(ValueReaderImpl(data), config)

    init {
        // Advertising Parameters
        val advertisingModeData: ByteArray = reader.readBytes(8)

        flags = reader.readUInt16()

        advertisingModeParameters = AdvertisingModeParameters(advertisingModeData)

        packetCount = reader.readUInt16()

        name = reader.readAsciiString(config.NAME_SIZE)

        val typeName = reader.readAsciiString(config.FRAME_TYPE_SIZE)
        type_ = Type.valueOf(typeName)

        shownInUI = type_ != Type.EMPTY

        rawAdvertisingContent = reader.readBytes(config.ADV_FORMAT_SIZE).also { data ->
            advertisingContent.putAll(type_.rawToContentConverter(data))
        }

        txPower = reader.readInt8()

        // RESERVED
        reader.readBytes(3)
    }

    override fun toBytes(): ByteArray {
        return advertisingModeParameters.data +
            UINT16.serialize(flags) +
            UINT16.serialize(packetCount) +
            ASCII_STRING.serialize(name, order = ByteOrder.BIG_ENDIAN).copyOf(config.NAME_SIZE) +
            ASCII_STRING.serialize(type_.name, order = ByteOrder.BIG_ENDIAN).copyOf(config.FRAME_TYPE_SIZE) +
            contentBytes().copyOf(config.ADV_FORMAT_SIZE) +
            INT8.serialize(txPower) +
            byteArrayOf(0x00, 0x00, 0x00)
    }
}
