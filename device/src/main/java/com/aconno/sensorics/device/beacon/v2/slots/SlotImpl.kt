package com.aconno.sensorics.device.beacon.v2.slots

import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slot.AdvertisingModeParameters.Mode.EVENT
import com.aconno.sensorics.device.beacon.Slot.AdvertisingModeParameters.Mode.INTERVAL
import com.aconno.sensorics.device.beacon.Slots.Config
import com.aconno.sensorics.domain.migrate.*
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.ASCII_STRING
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.FLOAT
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.INT8
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT16
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*


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

    override var dirty: Boolean = false
        get() = field || !contentBytes().contentEquals(rawAdvertisingContent)

    constructor(data: ByteArray, config: Config) : this(ValueReaderImpl(data), config)

    init {
        // Advertising Parameters
        val advertisingModeData: ByteArray = reader.readBytes(8)

        flags = reader.readUInt16()

        advertisingModeParameters = AdvertisingModeParametersImpl(advertisingModeData)

        packetCount = reader.readUInt16()

        name = reader.readAsciiString(config.NAME_SIZE)

        val typeName = reader.readAsciiString(config.FRAME_TYPE_SIZE)
        type_ = Type.valueOf(typeName)

        shownInUI = type_ != Type.EMPTY

        rawAdvertisingContent = reader.readBytes(config.ADV_FORMAT_SIZE).also { data ->
            advertisingContent.putAll(getAdvContentConverter(type_).rawToContentConverter(data))
        }

        txPower = reader.readInt8()

        // RESERVED
        reader.readBytes(3)
    }


    override fun toBytes(): ByteArray {
        return (advertisingModeParameters as AdvertisingModeParametersImpl).data +
            UINT16.serialize(flags) +
            UINT16.serialize(packetCount) +
            ASCII_STRING.serialize(name, order = ByteOrder.BIG_ENDIAN).copyOf(config.NAME_SIZE) +
            ASCII_STRING.serialize(type_.name, order = ByteOrder.BIG_ENDIAN).copyOf(config.FRAME_TYPE_SIZE) +
            contentBytes().copyOf(config.ADV_FORMAT_SIZE) +
            INT8.serialize(txPower) +
            byteArrayOf(0x00, 0x00, 0x00)
    }

    fun contentBytes(): ByteArray {
        return if (type_.hasAdvertisingContent) {
            (getAdvContentConverter(type_).contentToRawConverter)(advertisingContent).copyOf(advertisingContentMaxLength)
        } else {
            ByteArray(advertisingContentMaxLength)
        }
    }

    private fun getAdvContentConverter(type : Type) : AdvertisingContentConverter {
        return when(type) {
            Type.DEFAULT -> AdvertisingContentConverter.DEFAULT_CONVERTER
            Type.UID -> AdvertisingContentConverter.UID_CONVERTER
            Type.URL -> AdvertisingContentConverter.URL_CONVERTER
            Type.TLM -> AdvertisingContentConverter.TLM_CONVERTER
            Type.I_BEACON -> AdvertisingContentConverter.I_BEACON_CONVERTER
            Type.DEVICE_INFO -> AdvertisingContentConverter.DEVICE_INFO_CONVERTER
            Type.EMPTY -> AdvertisingContentConverter.EMPTY_CONVERTER
            Type.CUSTOM -> AdvertisingContentConverter.CUSTOM_CONVERTER
        }
    }

    class AdvertisingContentConverter(
        val rawToContentConverter: ((ByteArray) -> MutableMap<String, String>) = { mutableMapOf() },
        val contentToRawConverter: ((MutableMap<String, String>) -> ByteArray) = { ByteArray(0) }
    ) {

        companion object {
            val DEFAULT_CONVERTER = AdvertisingContentConverter({
                mutableMapOf<String, String>().apply {
                    put(KEY_ADVERTISING_CONTENT_DEFAULT_DATA, trimAdvertisement(it).toCompactHex())
                }
            }, {
                it[KEY_ADVERTISING_CONTENT_DEFAULT_DATA]?.hexStringToByteArray() ?: byteArrayOf()
            }
            )


            val UID_CONVERTER = AdvertisingContentConverter ({
                mutableMapOf<String, String>().apply {
                    put(KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID, it.copyOfRange(0, 10).toCompactHex())
                    put(KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID, it.copyOfRange(10, 16).toCompactHex())
                }
            }, {
                byteArrayOf(
                    0x02,
                    0x01,
                    0x06,
                    0x03,
                    0x03,
                    0xAA.toByte(),
                    0xFE.toByte(),
                    0x24,
                    0x16,
                    0xAA.toByte(),
                    0xFE.toByte()
                ) +
                        byteArrayOf(0x00, 0x10) + // TODO TODO TODO
                        (it[KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID] ?: "").hexStringToByteArray() +
                        (it[KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID] ?: "").hexStringToByteArray() +
                        byteArrayOf(0x00, 0x00)
            })


            val URL_CONVERTER = AdvertisingContentConverter({
                mutableMapOf<String, String>().apply {
                    put(
                        KEY_ADVERTISING_CONTENT_URL_URL, when (it[13].toInt()) {
                            0 -> "http://www."
                            1 -> "https://www."
                            2 -> "http://"
                            3 -> "https://"
                            else -> "invalid"
                        } + it.copyOfRange(14, it.size).toString(charset = Charset.defaultCharset())
                    )
                }
            }, {
                var url = it[KEY_ADVERTISING_CONTENT_URL_URL] ?: ""
                val bytes = byteArrayOf(
                    0x02,
                    0x01,
                    0x06,
                    0x03,
                    0x03,
                    0xAA.toByte(),
                    0xFE.toByte(),
                    0x03 + 0x03,
                    0x16,
                    0xAA.toByte(),
                    0xFE.toByte()
                ) +
                        byteArrayOf(0x10, 0x10) +
                        byteArrayOf(
                            when {
                                url.startsWith("http://www.") -> {
                                    url = url.replace("http://www.", "")
                                    0x00
                                }
                                url.startsWith("https://www.") -> {
                                    url = url.replace("https://www.", "")
                                    0x01
                                }
                                url.startsWith("http://") -> {
                                    url = url.replace("http://", "")
                                    0x02
                                }
                                url.startsWith("https://") -> {
                                    url = url.replace("https://", "")
                                    0x03
                                }
                                else -> 0x02
                            }
                        ) + url.substring(0, if (url.length > 17) 17 else url.length).toByteArray()
                bytes.apply {
                    this[7] = (this[7] + url.substring(
                        0,
                        if (url.length > 17) 17 else url.length
                    ).length).toByte()
                }
            }
            )


            val TLM_CONVERTER = AdvertisingContentConverter({
                mutableMapOf<String, String>()
            }, {
                // TODO: Embed parameter values in here, talk to Dominik
                byteArrayOf(
                    0x02,
                    0x01,
                    0x06,
                    0x03,
                    0x03,
                    0xAA.toByte(),
                    0xFE.toByte(),
                    0x03 + 0x03,
                    0x16,
                    0xAA.toByte(),
                    0xFE.toByte()
                ) +
                        byteArrayOf(
                            0x20,
                            0x00,
                            0x00,
                            0x01,
                            0x00,
                            0x02,
                            0x12,
                            0x34,
                            0x56,
                            0x78,
                            0x87.toByte(),
                            0x65,
                            0x43,
                            0x21
                        )
            }
            )

            val I_BEACON_CONVERTER = AdvertisingContentConverter({
                mutableMapOf<String, String>().apply {
                    put(
                        KEY_ADVERTISING_CONTENT_IBEACON_UUID,
                        bytesToUUID(it.copyOfRange(9, 25)).toString()
                    )
                    put(
                        KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
                        UINT16.deserialize(it.copyOfRange(25, 27)).toString()
                    )
                    put(
                        KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
                        UINT16.deserialize(it.copyOfRange(27, 29)).toString()
                    )
                }
            }, {

                val uuid: UUID =
                    try {
                        UUID.fromString(
                            it[KEY_ADVERTISING_CONTENT_IBEACON_UUID]
                                ?: DEFAULT_ADVERTISING_CONTENT_IBEACON_UUID
                        )
                    } catch (ex : IllegalArgumentException) {
                        UUID.fromString(DEFAULT_ADVERTISING_CONTENT_IBEACON_UUID)
                    }

                val major: Int = if(it[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR]?.isNotEmpty() != true) {
                    0
                } else {
                    it[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR]?.toInt() ?: 0
                }
                val minor: Int = if(it[KEY_ADVERTISING_CONTENT_IBEACON_MINOR]?.isNotEmpty() != true) {
                    0
                } else {
                    it[KEY_ADVERTISING_CONTENT_IBEACON_MINOR]?.toInt() ?: 0
                }

                byteArrayOf(0x02, 0x01, 0x06, 0x1A, 0xFF.toByte(), 0x00, 0x4C, 0x02, 0x15) +
                        uuid.toBytes() +
                        UINT16.serialize(major) +
                        UINT16.serialize(minor) +
                        byteArrayOf(0x01)
            }

            )


            val DEVICE_INFO_CONVERTER = AdvertisingContentConverter ()

            val EMPTY_CONVERTER = AdvertisingContentConverter()

            val CUSTOM_CONVERTER = AdvertisingContentConverter({ advertisement ->
                mutableMapOf<String, String>().apply {
                    advertisementToMap(advertisement)[0xFF.toByte()]?.let {
                        put(KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM, it.toCompactHex())
                    }
                }
            }, {
                val msd = it[KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM]?.hexStringToByteArray()
                    ?: byteArrayOf()
                byteArrayOf((0x01 + msd.size).toByte(), 0xFF.toByte()) + msd
            }
            )
        }

    }

    class AdvertisingModeParametersImpl(val data : ByteArray) : AdvertisingModeParameters{
        override var parameterId: Int
            get() = UINT16.deserialize(data, 0)
            set(value) {
                UINT16.serialize(value).let { serializedData ->
                    data[0] = serializedData[0]
                    data[1] = serializedData[1]
                }
            }

        override var sign: AdvertisingModeParameters.Sign
            get() = AdvertisingModeParameters.Sign.values().find { getSignValue(it) == UINT16.deserialize(data, 2) } ?: let {
                sign = AdvertisingModeParameters.Sign.EQUAL
                sign
            }
            set(value) {
                UINT16.serialize(getSignValue(value)).let { serializedData ->
                    data[2] = serializedData[0]
                    data[3] = serializedData[1]
                }
            }

        override var thresholdInt: Long
            get() = UINT32.deserialize(data, 4)
            set(value) {
                UINT32.serialize(value).forEachIndexed { i, b -> data[i + 4] = b }
            }

        override var thresholdFloat: Float
            get() = FLOAT.deserialize(data, 4)
            set(value) {
                FLOAT.serialize(value).forEachIndexed { i, b -> data[i + 4] = b }
            }

        override var interval: Long
            get() = UINT32.deserialize(data, 0)
            set(value) {
                UINT32.serialize(value).forEachIndexed { i, b -> data[i] = b }
            }

        fun clear() {
            for (i in 0 until data.size) {
                data[i] = 0x00
            }
        }

        private fun getSignValue(sign : AdvertisingModeParameters.Sign) : Int {
            return when(sign) {
                AdvertisingModeParameters.Sign.LESS -> 0x00
                AdvertisingModeParameters.Sign.LESS_OR_EQUAL -> 0x01
                AdvertisingModeParameters.Sign.EQUAL -> 0x02
                AdvertisingModeParameters.Sign.GREATER_OR_EQUAL -> 0x03
                AdvertisingModeParameters.Sign.GREATER -> 0x04
            }
        }

        companion object {

            val SCALE: List<Int> = listOf(50) + (100..900 step 100) + // MILLIS
                    ((1..15) + listOf(15) + (20..50 step 10)).map { it * 1000 } + // SECONDS
                    ((1..15) + listOf(15) + (20..50 step 10)).map { it * 1000 * 60 } + // MINUTES
                    ((1..24).map { it * 1000 * 60 * 60 }) // HOURS
        }
    }

    companion object {
        fun advertisementToMap(advertisement: ByteArray): Map<Byte, ByteArray> {
            val map: MutableMap<Byte, MutableList<Byte>> = mutableMapOf()
            var currentId: Byte? = null
            var lengthLeft = 0
            for (i in 0 until advertisement.size) {
                if (lengthLeft == 0) {
                    lengthLeft = advertisement[i].toInt()
                } else {
                    if (currentId == null) {
                        currentId = advertisement[i]
                        map[currentId] = mutableListOf()
                    } else {
                        map[currentId]?.add(advertisement[i])
                    }
                    lengthLeft--
                }
            }

            return map.entries.associate { it.key to it.value.toByteArray() }
        }

        fun advertisementMapToBytes(map: Map<Byte, ByteArray>): ByteArray {
            return map.flatMap { entry ->
                listOf((entry.value.size + 1).toByte(), entry.key) + entry.value.toList()
            }.toByteArray()
        }

        fun trimAdvertisement(advertisement: ByteArray): ByteArray {
            return advertisementMapToBytes(advertisementToMap(advertisement))
        }
    }
}
