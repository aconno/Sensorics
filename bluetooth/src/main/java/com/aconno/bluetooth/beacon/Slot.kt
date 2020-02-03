package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.BleReadableWritable
import com.aconno.bluetooth.CharacteristicReadTask
import com.aconno.bluetooth.CharacteristicWriteTask
import com.aconno.bluetooth.Task
import timber.log.Timber
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*


class Slot(
        type: Type = Type.EMPTY,
        val slotAdvertisingContent: MutableMap<String, Any> = mutableMapOf()
) : BleReadableWritable {
    /**
     * Internal type variable
     */
    private var type: Type = Type.EMPTY

    /**
     * Initial Raw Advertising Content
     */
    private var rawAdvertisingContent: ByteArray = byteArrayOf()

    /**
     * Maximum available advertising content length (not max advertising BLE length)
     */
    private var advertisingContentMaxLength: Int = 62

    /**
     * Determines if the slot data has changed
     */
    private var dirty: Boolean = false
        get() = field || !contentBytes().contentEquals(rawAdvertisingContent)

    constructor(value: ByteArray) : this(Type.valueOf(
            ValueConverter.UTF8STRING.converter.deserialize(
                    value.copyOfRange(0, value.stringLength()), ByteOrder.BIG_ENDIAN
            ).toString()
    ))

    init {
        this.type = type
    }

    /**
     * Gets the type of this slot
     */
    fun getType() = this.type

    /**
     * Sets the type of this slot
     */
    fun setType(type: Type) {
        this.type = type
        dirty = true
    }

    /**
     * Generates content bytes from the provided slot advertising content if applicable
     */
    fun contentBytes(): ByteArray {
        return if (type.hasAdvertisingContent) {
            (type.contentToRawConverter!!)(slotAdvertisingContent).copyOf(advertisingContentMaxLength)
        } else {
            ByteArray(advertisingContentMaxLength)
        }
    }

    override fun read(): List<Task> {
        return listOf(AdvertisingContentReadTask())
    }

    override fun write(full: Boolean): List<Task> {
        return if (dirty || full) {
            listOf(SlotTypeWriteTask(), AdvertisingContentWriteTask())
        } else listOf()
    }

    inner class SlotTypeWriteTask : CharacteristicWriteTask(
            characteristicUUID = SLOT_TYPE_UUID,
            value = ValueConverter.UTF8STRING.converter.serialize(type.name, order = ByteOrder.BIG_ENDIAN).copyOf(20)
    ) {
        override fun onSuccess() {
            Timber.i("Written Slot Type")
        }
    }

    inner class AdvertisingContentReadTask : CharacteristicReadTask(characteristicUUID = SLOT_DATA_UUID) {
        override fun onSuccess(value: ByteArray) {
            advertisingContentMaxLength = value.size
            rawAdvertisingContent = value
            type.rawToContentConverter?.let {
                slotAdvertisingContent.putAll(it(value))
            }
        }
    }

    inner class AdvertisingContentWriteTask : CharacteristicWriteTask(
            characteristicUUID = SLOT_DATA_UUID,
            value = contentBytes()
    ) {
        override fun onSuccess() {
            Timber.i("Written Slot Contents")
        }
    }

    enum class Type(
            val tabName: String,
            val hasAdvertisingContent: Boolean = false,
            val rawToContentConverter: ((ByteArray) -> MutableMap<String, Any>)? = null,
            val contentToRawConverter: ((MutableMap<String, Any>) -> ByteArray)? = null
    ) {
        DEFAULT("DEFAULT", true, {
            mutableMapOf<String, Any>().apply {
                put(KEY_ADVERTISING_CONTENT_DEFAULT_DATA, trimAdvertisement(it))
            }
        }, {
            it[KEY_ADVERTISING_CONTENT_DEFAULT_DATA] as ByteArray
        }),
        UID("UID", true, {
            mutableMapOf<String, Any>().apply {
                put(KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID, it.copyOfRange(0, 10).toCompactHex())
                put(KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID, it.copyOfRange(10, 16).toCompactHex())
            }
        }, {
            byteArrayOf(0x02, 0x01, 0x06, 0x03, 0x03, 0xAA.toByte(), 0xFE.toByte(), 0x24, 0x16, 0xAA.toByte(), 0xFE.toByte()) +
                    byteArrayOf(0x00, 0x10) + // TODO TODO TODO
                    it[KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID].toString().hexStringToByteArray() +
                    it[KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID].toString().hexStringToByteArray() +
                    byteArrayOf(0x00, 0x0)
        }),
        URL("URL", true, {
            mutableMapOf<String, Any>().apply {
                put(KEY_ADVERTISING_CONTENT_URL_URL, when (it[13].toInt()) {
                    0 -> "http://www."
                    1 -> "https://www."
                    2 -> "http://"
                    3 -> "https://"
                    else -> "invalid"
                } + it.copyOfRange(14, it.size).toString(charset = Charset.defaultCharset()))
            }
        }, {
            var url = it[KEY_ADVERTISING_CONTENT_URL_URL].toString()
            val bytes = byteArrayOf(0x02, 0x01, 0x06, 0x03, 0x03, 0xAA.toByte(), 0xFE.toByte(), 0x03 + 0x03, 0x16, 0xAA.toByte(), 0xFE.toByte()) +
                    byteArrayOf(0x10, 0x10) +
                    byteArrayOf(when {
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
                        else -> 0x04
                    }) + url.substring(0, if (url.length > 17) 17 else url.length).toByteArray()
            bytes.apply {
                this[7] = (this[7] + url.substring(0, if (url.length > 17) 17 else url.length).length).toByte()
            }
        }),
        TLM("TLM", true, {
            mutableMapOf<String, Any>()
        }, {
            // TODO: Embed parameter values in here, talk to Dominik
            kotlin.byteArrayOf(0x02, 0x01, 0x06, 0x03, 0x03, 0xAA.toByte(), 0xFE.toByte(), 0x03 + 0x03, 0x16, 0xAA.toByte(), 0xFE.toByte()) +
                    kotlin.byteArrayOf(0x20, 0x00, 0x00, 0x01, 0x00, 0x02, 0x12, 0x34, 0x56, 0x78, 0x87.toByte(), 0x65, 0x43, 0x21)
        }),
        I_BEACON("iBeacon", true, {
            mutableMapOf<String, Any>().apply {
                UUID.randomUUID()
                put(KEY_ADVERTISING_CONTENT_IBEACON_UUID, bytesToUUID(it.copyOfRange(9, 25)))
                put(KEY_ADVERTISING_CONTENT_IBEACON_MAJOR, ValueConverter.UINT16.converter.deserialize(it.copyOfRange(25, 27)) as Int)
                put(KEY_ADVERTISING_CONTENT_IBEACON_MINOR, ValueConverter.UINT16.converter.deserialize(it.copyOfRange(27, 29)) as Int)
            }
        }, {
            val uuid: UUID = UUID.fromString(it[KEY_ADVERTISING_CONTENT_IBEACON_UUID] as String? ?: "00000000-0000-0000-0000-000000000000")
            val major: Int = it[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR].toString().toIntOrNull() ?: 0
            val minor: Int = it[KEY_ADVERTISING_CONTENT_IBEACON_MINOR].toString().toIntOrNull() ?: 0
            byteArrayOf(0x02, 0x01, 0x06, 0x1A, 0xFF.toByte(), 0x00, 0x4C, 0x02, 0x15) +
                    uuid.toBytes() +
                    ValueConverter.UINT16.converter.serialize(major) +
                    ValueConverter.UINT16.converter.serialize(minor) +
                    kotlin.byteArrayOf(0x01)
        }),
        DEVICE_INFO("DeviceInfo", false),
        EMPTY("-", false),
        CUSTOM("Custom", true, {
            mutableMapOf<String, Any>().apply {
                put(KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM, it.copyOfRange(2, it.size).let {
                    var lastZeroByte: Int = it.size
                    for (i in it.size - 1 downTo 0) {
                        if (it[i] != 0x00.toByte()) break
                        lastZeroByte = i
                    }
                    it.copyOfRange(0, lastZeroByte)
                }.toAsciiHexEscaped())
            }
        }, {
            val msd = (it[KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM] as String).toHexUsingAsciiEscaped()
            byteArrayOf((0x01 + msd.size).toByte(), 0xFF.toByte()) + msd
        })
    }

    companion object {
        const val EXTRA_BEACON_SLOT_POSITION = "BEACON_SLOT_POSITION"
        const val KEY_ADVERTISING_CONTENT_DEFAULT_DATA = "ADVERTISING_CONTENT_DEFAULT_DATA"
        const val KEY_ADVERTISING_CONTENT_IBEACON_UUID = "ADVERTISING_CONTENT_IBEACON_UUID"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MAJOR = "ADVERTISING_CONTENT_IBEACON_MAJOR"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MINOR = "ADVERTISING_CONTENT_IBEACON_MINOR"
        const val KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID = "ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        const val KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID = "ADVERTISING_CONTENT_UID_INSTANCE_ID"
        const val KEY_ADVERTISING_CONTENT_URL_URL = "ADVERTISING_CONTENT_UID_URL_URL"
        const val KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM = "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM"


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
