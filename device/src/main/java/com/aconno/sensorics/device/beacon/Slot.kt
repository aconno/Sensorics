package com.aconno.sensorics.device.beacon

import com.aconno.sensorics.device.beacon.v2.parameters.getAsGivenTypeOrNull
import com.aconno.sensorics.domain.migrate.*
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.FLOAT
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT16
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*

/**
 * Abstract Slot base class
 *
 * @property type_ slot type
 * @property advertisingContent slot advertising content
 */
abstract class Slot(
    protected open var type_: Type = Type.EMPTY,
    open val advertisingContent: MutableMap<String, String> = mutableMapOf()
) {

    /**
     * Shown in UI TODO: Move this somewhere else
     */
    abstract var shownInUI: Boolean

    /**
     * Initial Raw Advertising Content
     */
    protected open var rawAdvertisingContent: ByteArray = byteArrayOf()

    /**
     * Maximum available advertising content length (not max advertising BLE length)
     */
    protected open var advertisingContentMaxLength: Int = 62

    /**
     * Slot name
     */
    abstract var name: String

    /**
     * Is slot read only
     */
    abstract val readOnly: Boolean

    /**
     * Is slot active
     */
    abstract var active: Boolean

    /**
     * Number of packets sent per advertisement
     */
    abstract var packetCount: Int

    /**
     * TX Power
     */
    abstract var txPower: Byte

    /**
     * Advertising mode parameters
     */
    abstract var advertisingMode: AdvertisingModeParameters.Mode

    /**
     * Advertising mode parameters
     */
    abstract var advertisingModeParameters: AdvertisingModeParameters

    /**
     * Determines if the slot data has changed
     */
    private var dirty: Boolean = false
        get() = field || !contentBytes().contentEquals(rawAdvertisingContent)

    constructor(value: ByteArray) : this(
        Type.valueOf(
            ValueConverters.ASCII_STRING.deserialize(
                value.copyOfRange(0, value.stringLength()), order = ByteOrder.BIG_ENDIAN
            )
        )
    )

    /**
     * Gets the type of this slot
     */
    fun getType() = this.type_

    /**
     * Sets the type of this slot
     */
    fun setType(type: Type) {
        this.type_ = type
        dirty = true
    }

    /**
     * Generates content bytes from the provided slot advertising content if applicable
     */
    fun contentBytes(): ByteArray {
        return if (type_.hasAdvertisingContent) {
            (type_.contentToRawConverter)(advertisingContent).copyOf(advertisingContentMaxLength)
        } else {
            ByteArray(advertisingContentMaxLength)
        }
    }

    /**
     * Converts the Slot to its C struct representation
     *
     * @return
     */
    abstract fun toBytes(): ByteArray


    fun toJson(): JsonElement {
        return JsonObject().apply {
            this.addProperty("name", name)
            this.addProperty("type", getType().name)
            this.add("advertisingContent", gson.toJsonTree(advertisingContent))
            this.addProperty("readOnly", readOnly)
            this.addProperty("active", active)
            this.addProperty("txPower", txPower)
            this.addProperty("packetCount", packetCount)
            this.addProperty("advertisingMode", advertisingMode.toString())
            when (advertisingMode) {
                AdvertisingModeParameters.Mode.EVENT -> {
                    this.add("advertisingModeParameters", JsonObject().apply {
                        this.addProperty("parameterId", advertisingModeParameters.parameterId)
                        this.addProperty("sign", advertisingModeParameters.sign.toString())
                        this.addProperty("thresholdFloat", advertisingModeParameters.thresholdFloat)
                        this.addProperty("thresholdInt", advertisingModeParameters.thresholdInt)
                    })
                }
                AdvertisingModeParameters.Mode.INTERVAL -> {
                    this.add("advertisingModeParameters", JsonObject().apply {
                        this.addProperty("interval", advertisingModeParameters.interval)
                    })
                }
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonObject) {

        val name = obj.getStringOrNull("name") ?: throw throwMissingArg("name")

        val active = obj.getBooleanOrNull("active") ?: throw throwMissingArg("active")

        val type = obj.getStringOrNull("type")
            ?: throwMissingArg("Type")

        val advertisingContent: Map<String, String> =
            obj.getObjectOrNull("advertisingContent")?.let {
                try {
                    gson.fromJson<Map<String, String>>(it, mapStringStringTypeToken)
                } catch (e: Exception) {
                    throw IllegalArgumentException(
                        "Invalid advertisingContent format!", e
                    )
                }
            } ?: throwMissingArg("Advertising content")

        val txPower = obj.getNumberOrNull("txPower")
            ?: throw IllegalArgumentException("Tx power content is missing in slot or it is not a number")
        val packetCount = obj.getNumberOrNull("packetCount")
            ?: throw IllegalArgumentException("packet count is missing in slot or it is not a number")

        this.name = name
        this.setType(Type.valueOf(type))
        this.advertisingContent.clear()
        this.advertisingContent.putAll(advertisingContent)
        this.active = active
        this.txPower = getAsGivenTypeOrNull(txPower.toString(), Byte::class.java)
            ?: throw IllegalArgumentException("txPower is $txPower and it is not a byte")
        this.packetCount = getAsGivenTypeOrNull(packetCount.toString(), Int::class.java)
            ?: throw IllegalArgumentException("packetCount is $packetCount and it is not a integer")
        obj.getStringOrNull("advertisingMode")?.let {
            advertisingMode = AdvertisingModeParameters.Mode.valueOf(it)
        } ?: throwMissingArg("advertising mode")

        obj.getObjectOrNull("advertisingModeParameters")?.let {
            when (advertisingMode) {
                AdvertisingModeParameters.Mode.EVENT -> {
                    val parameterId = it.getNumberOrNull("parameterId")
                        ?: throwMissingArg("advertising parameter id")
                    val sign = it.getStringOrNull("sign") ?: throwMissingArg("advertising sign")
                    val thresholdInt =
                        it.getNumberOrNull("thresholdInt") ?: throwMissingArg("threshold")
                    // todo add threshold float

                    advertisingModeParameters.parameterId =
                        getAsGivenTypeOrNull(parameterId.toString(), Int::class.java)
                            ?: throw  IllegalArgumentException("parameter id $parameterId is not int")
                    advertisingModeParameters.sign = AdvertisingModeParameters.Sign.valueOf(sign)
                    advertisingModeParameters.thresholdInt =
                        getAsGivenTypeOrNull(thresholdInt.toString(), Long::class.java)
                            ?: throw  IllegalArgumentException("thresholdInt $parameterId is not long")

                }
                AdvertisingModeParameters.Mode.INTERVAL -> {
                    val interval =
                        it.getNumberOrNull("interval") ?: throwMissingArg("advertising interval")
                    advertisingModeParameters.interval =
                        getAsGivenTypeOrNull(interval.toString(), Long::class.java)
                            ?: throw  IllegalArgumentException("interval $interval is not long")
                }
            }
        }
    }

    private fun throwMissingArg(missingArg: String): Nothing =
        throw IllegalArgumentException("$missingArg mode is missing in slot")


    enum class Type(
        val tabName: String,
        val hasAdvertisingContent: Boolean = false,
        val rawToContentConverter: ((ByteArray) -> MutableMap<String, String>) = { mutableMapOf() },
        val contentToRawConverter: ((MutableMap<String, String>) -> ByteArray) = { ByteArray(0) }
    ) {
        DEFAULT("DEFAULT", true, {
            mutableMapOf<String, String>().apply {
                put(KEY_ADVERTISING_CONTENT_DEFAULT_DATA, trimAdvertisement(it).toCompactHex())
            }
        }, {
            it[KEY_ADVERTISING_CONTENT_DEFAULT_DATA]?.hexStringToByteArray() ?: byteArrayOf()
        }),
        UID("UID", true, {
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
        }),
        URL("URL", true, {
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
        }),
        TLM("TLM", true, {
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
        }),
        I_BEACON("iBeacon", true, {
            mutableMapOf<String, String>().apply {
                put(
                    KEY_ADVERTISING_CONTENT_IBEACON_UUID,
                    bytesToUUID(it.copyOfRange(9, 25)).toString()
                )
                put(
                    KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
                    ValueConverters.UINT16.deserialize(it.copyOfRange(25, 27)).toString()
                )
                put(
                    KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
                    ValueConverters.UINT16.deserialize(it.copyOfRange(27, 29)).toString()
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
                    ValueConverters.UINT16.serialize(major) +
                    ValueConverters.UINT16.serialize(minor) +
                    byteArrayOf(0x01)
        }),
        DEVICE_INFO("DeviceInfo", false),
        EMPTY("-", false),
        CUSTOM("Custom (MSD)", true, { advertisement ->
            mutableMapOf<String, String>().apply {
                advertisementToMap(advertisement)[0xFF.toByte()]?.let {
                    put(KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM, it.toCompactHex())
                }
            }
        }, {
            val msd = it[KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM]?.hexStringToByteArray()
                ?: byteArrayOf()
            byteArrayOf((0x01 + msd.size).toByte(), 0xFF.toByte()) + msd
        })
    }

    class AdvertisingModeParameters(val data: ByteArray = ByteArray(8)) {
        var parameterId: Int
            get() = UINT16.deserialize(data, 0)
            set(value) {
                UINT16.serialize(value).let { serializedData ->
                    data[0] = serializedData[0]
                    data[1] = serializedData[1]
                }
            }

        var sign: Sign
            get() = Sign.values().find { it.value == UINT16.deserialize(data, 2) } ?: let {
                sign = Sign.EQUAL
                sign
            }
            set(value) {
                UINT16.serialize(value.value).let { serializedData ->
                    data[2] = serializedData[0]
                    data[3] = serializedData[1]
                }
            }

        var thresholdInt: Long
            get() = UINT32.deserialize(data, 4)
            set(value) {
                UINT32.serialize(value).forEachIndexed { i, b -> data[i + 4] = b }
            }

        var thresholdFloat: Float
            get() = FLOAT.deserialize(data, 4)
            set(value) {
                FLOAT.serialize(value).forEachIndexed { i, b -> data[i + 4] = b }
            }

        var interval: Long
            get() = UINT32.deserialize(data, 0)
            set(value) {
                UINT32.serialize(value).forEachIndexed { i, b -> data[i] = b }
            }

        enum class Sign(val value: Int) {
            LESS(0x00), LESS_OR_EQUAL(0x01), EQUAL(0x02), GREATER_OR_EQUAL(0x03), GREATER(0x04)
        }

        enum class Mode(val id: Int) {
            INTERVAL(0x00), EVENT(0x01)
        }

        fun clear() {
            for (i in 0 until data.size) {
                data[i] = 0x00
            }
        }

        class Factory {
            companion object {
                fun createFromModeId(id: Int): AdvertisingModeParameters {
                    return Mode.values().find { it.id == id }?.let {
                        AdvertisingModeParameters()
                    } ?: throw NotImplementedError("This advertising mode has not been implemented")
                }
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
        private val gson = GsonBuilder().create()
        private val mapStringStringTypeToken = object : TypeToken<Map<String, String>>() {}.type

        const val KEY_ADVERTISING_CONTENT_DEFAULT_DATA = "ADVERTISING_CONTENT_DEFAULT_DATA"
        const val KEY_ADVERTISING_CONTENT_IBEACON_UUID = "ADVERTISING_CONTENT_IBEACON_UUID"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MAJOR = "ADVERTISING_CONTENT_IBEACON_MAJOR"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MINOR = "ADVERTISING_CONTENT_IBEACON_MINOR"
        const val KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID = "ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        const val KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID = "ADVERTISING_CONTENT_UID_INSTANCE_ID"
        const val KEY_ADVERTISING_CONTENT_URL_URL = "ADVERTISING_CONTENT_URL_URL"
        const val KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM = "ADVERTISING_CONTENT_CUSTOM_CUSTOM"

        const val DEFAULT_ADVERTISING_CONTENT_IBEACON_UUID = "00000000-0000-0000-0000-000000000000"


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
