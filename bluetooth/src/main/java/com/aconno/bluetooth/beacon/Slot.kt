package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.UUIDProvider
import com.aconno.bluetooth.tasks.ReadTask
import com.aconno.bluetooth.tasks.Task
import com.aconno.bluetooth.tasks.WriteTask
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*

fun ByteArray.stringLength(offset: Int = 0): Int {
    for (i in offset until this.size) if (this[i] == 0x00.toByte()) return i - offset + 1
    return this.size - offset
}

fun UUID.toBytes(): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(this.mostSignificantBits)
    bb.putLong(this.leastSignificantBits)
    return bb.array()
}

fun bytesToUUID(bytes: ByteArray): UUID {
    val bb = ByteBuffer.wrap(bytes)
    return UUID(bb.long, bb.long)
}

data class Slot(
    var type: Type = Type.EMPTY,
    val slotAdvertisingContent: MutableMap<String, Any> = mutableMapOf(),
    var advertisingInterval: Int = 0,
    var rssi1m: Int = 0,
    var radioTx: Int = 0,
    var triggerEnabled: Boolean = false,
    var triggerType: TriggerType = TriggerType.DOUBLE_TAP
) {
    val dirty: Boolean = false

    constructor(value: ByteArray) : this(
        Type.valueOf(
            ValueConverter.UTF8STRING.converter.deserialize(
                value.copyOfRange(0, value.stringLength()), ByteOrder.BIG_ENDIAN
            ).toString()
        )
    )

    fun contentBytes(): ByteArray {
        return if (type.hasAdvertisingContent)
            type.contentToRawConverter!!.invoke(slotAdvertisingContent)
        else ByteArray(25)
    }

    fun readAdvertisementData(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B003")) {
            override fun onSuccess(value: ByteArray) {
                type.rawtoContentConverter?.let {
                    slotAdvertisingContent.putAll(it(value))
                }
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun readAdvertisingInterval(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B004")) {
            override fun onSuccess(value: ByteArray) {
                advertisingInterval =
                    com.aconno.bluetooth.ValueConverter.UINT16.converter.deserialize(
                        value.copyOfRange(
                            0,
                            2
                        )
                    ).toString().toInt()
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun readRssi1m(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B005")) {
            override fun onSuccess(value: ByteArray) {
                rssi1m = com.aconno.bluetooth.ValueConverter.SINT16.converter.deserialize(
                    value.copyOfRange(
                        0,
                        2
                    )
                ).toString().toInt()
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun readRadioTx(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B006")) {
            override fun onSuccess(value: ByteArray) {
                radioTx = com.aconno.bluetooth.ValueConverter.SINT16.converter.deserialize(
                    value.copyOfRange(
                        0,
                        2
                    )
                ).toString().toInt()
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun readTriggerEnabled(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B007")) {
            override fun onSuccess(value: ByteArray) {
                triggerEnabled = com.aconno.bluetooth.ValueConverter.BOOLEAN.converter.deserialize(
                    byteArrayOf(value[0])
                ).toString().toBoolean()
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun readTriggerType(): Task {
        return object : ReadTask(UUIDProvider.provideFullUUID("B008")) {
            override fun onSuccess(value: ByteArray) {
                // TODO: Domingo ne zna programirat
                try {
                    triggerType =
                        TriggerType.values()[com.aconno.bluetooth.ValueConverter.UINT8.converter.deserialize(
                            byteArrayOf(value[0])
                        ).toString().toInt()]
                } catch (e: Exception) {
                    triggerType = TriggerType.DOUBLE_TAP
                }
            }

            override fun onError(error: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    fun read(): List<Task> {
        return listOf(
            readAdvertisementData(),
            readAdvertisingInterval(),
            readRadioTx(),
            readRssi1m(),
            readTriggerType(),
            readTriggerEnabled()
        )
    }

    fun write(): List<Task> {
        return listOf(
            object : WriteTask(
                UUIDProvider.provideFullUUID("B002"),
                ValueConverter.UTF8STRING.converter.serialize(
                    type.name,
                    order = ByteOrder.BIG_ENDIAN
                ).extendOrShorten(20)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Type")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B003"),
                contentBytes().extendOrShorten(62)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Contents")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B004"),
                ValueConverter.UINT16.converter.serialize(advertisingInterval)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Advertising Interval")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B005"),
                ValueConverter.SINT16.converter.serialize(rssi1m)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot RSSI")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B006"),
                ValueConverter.SINT16.converter.serialize(radioTx)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Radio TX")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B007"),
                ValueConverter.BOOLEAN.converter.serialize(triggerEnabled)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Trigger Enabled")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            },
            object : WriteTask(
                UUIDProvider.provideFullUUID("B008"),
                ValueConverter.UINT8.converter.serialize(triggerType.id)
            ) {
                override fun onSuccess() {
                    Timber.i("Written Slot Trigger Type")
                }

                override fun onError(error: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        )
    }

    enum class Type(
        val tabName: String,
        val hasAdvertisingContent: Boolean = false,
        val rawtoContentConverter: ((ByteArray) -> MutableMap<String, Any>)? = null,
        val contentToRawConverter: ((MutableMap<String, Any>) -> ByteArray)? = null
    ) {
        UID("UID", true, {
            mutableMapOf<String, Any>().apply {
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
                    it[KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID].toString().hexStringToByteArray() +
                    it[KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID].toString().hexStringToByteArray() +
                    byteArrayOf(0x00, 0x0)
        }),
        URL("URL", true, {
            mutableMapOf<String, Any>().apply {
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
            var url = it[KEY_ADVERTISING_CONTENT_URL_URL].toString()
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
                            else -> 0x04
                        }
                    ) + url.substring(0, if (url.length > 17) 17 else url.length).toByteArray()
            bytes.apply {
                this[7] = (this[7] + url.substring(
                    0,
                    if (url.length > 17) 17 else url.length
                ).length).toByte()
            }
        }),
        TLM("TLM", false),
        I_BEACON("iBeacon", true, {
            mutableMapOf<String, Any>().apply {
                UUID.randomUUID()
                put(KEY_ADVERTISING_CONTENT_IBEACON_UUID, bytesToUUID(it.copyOfRange(9, 25)))
                put(
                    KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
                    ValueConverter.UINT16.converter.deserialize(it.copyOfRange(25, 27)) as Int
                )
                put(
                    KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
                    ValueConverter.UINT16.converter.deserialize(it.copyOfRange(27, 29)) as Int
                )
            }
        }, {
            val uuid: UUID = UUID.fromString(it[KEY_ADVERTISING_CONTENT_IBEACON_UUID].toString())
            val major: Int = it[KEY_ADVERTISING_CONTENT_IBEACON_MAJOR].toString().toInt()
            val minor: Int = it[KEY_ADVERTISING_CONTENT_IBEACON_MINOR].toString().toInt()
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
                put(
                    KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
                    it.copyOfRange(2, it.size).toString(charset = Charset.forName("ASCII")).trim(
                        0x00.toChar()
                    )
                )
            }
        }, {
            val msd =
                (it[KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM] as String).toByteArray(Charset.defaultCharset())
            byteArrayOf((0x01 + msd.size).toByte(), 0xFF.toByte()) + msd
        })
    }

    enum class TriggerType(
        val triggerName: String,
        val id: Int
    ) {
        DOUBLE_TAP("Button Double Tap", 0),
        TRIPLE_TAP("Button Triple Tap", 1)
    }

    companion object {
        const val EXTRA_BEACON_SLOT_POSITION = "com.aconno.beaconapp.BEACON_SLOT_POSITION"
        const val KEY_ADVERTISING_CONTENT_IBEACON_UUID =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_IBEACON_UUID"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MAJOR =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_IBEACON_MAJOR"
        const val KEY_ADVERTISING_CONTENT_IBEACON_MINOR =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_IBEACON_MINOR"
        const val KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        const val KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_UID_INSTANCE_ID"
        const val KEY_ADVERTISING_CONTENT_URL_URL =
            "com.aconno.beaconapp.ADVERTISING_CONTENT_UID_URL_URL"
        const val KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM =
            "com.aconno.beaconapp.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM"
    }

}
