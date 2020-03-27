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
    protected abstract var dirty: Boolean


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
     * Converts slot to its byte representation
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
        val hasAdvertisingContent: Boolean = false
    ) {
        DEFAULT("DEFAULT", true),
        UID("UID", true),
        URL("URL", true),
        TLM("TLM", true),
        I_BEACON("iBeacon", true),
        DEVICE_INFO("DeviceInfo", false),
        EMPTY("-", false),
        CUSTOM("Custom (MSD)", true)
    }

    interface AdvertisingModeParameters {
        var parameterId: Int

        var sign: Sign

        var thresholdInt: Long

        var thresholdFloat: Float

        var interval: Long

        enum class Sign {
            LESS, LESS_OR_EQUAL, EQUAL, GREATER_OR_EQUAL, GREATER
        }

        enum class Mode {
            INTERVAL, EVENT
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


    }

}
