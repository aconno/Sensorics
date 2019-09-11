package com.aconno.sensorics.device.beacon.v2.arbitrarydata

import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicReadTask
import com.aconno.sensorics.device.bluetooth.tasks.CharacteristicWriteTask
import com.aconno.sensorics.domain.UUIDProvider
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UINT32
import com.aconno.sensorics.domain.migrate.ValueConverters.Companion.UTF8_STRING
import com.aconno.sensorics.domain.migrate.ValueReader
import com.aconno.sensorics.domain.migrate.ValueReaderImpl
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Type
import java.nio.ByteOrder
import java.util.*
import java.util.zip.CRC32


class ArbitraryDataImpl(
    var serviceUuid: UUID = DEFAULT_ARBITRARY_DATA_SERVICE_UUID,
    var uuid: UUID = DEFAULT_ARBITRARY_DATA_CHARACTERISTIC_UUID
) : ArbitraryData(0) {
    override val dirty: Boolean
        get() = gson.toJson(this) != initialSerialized

    val gson: Gson = GsonBuilder().create()
    var initialSerialized: String = ""

    val type: Type = object : TypeToken<Map<String, String>>() {}.type

    override fun read(taskProcessor: BluetoothTaskProcessor) {
        taskProcessor.queueTask(object : CharacteristicReadTask(
            name = "Arbitrary Data Read Task",
            serviceUUID = serviceUuid,
            characteristicUUID = uuid
        ) {
            var data: ByteArray = byteArrayOf()
            var totalSize: Int = 0

            override fun onSuccess(value: ByteArray) {
                data += value
                val reader: ValueReader = ValueReaderImpl(data)
                totalSize = reader.readUInt32().toInt()

                if (data.size < totalSize) {
                    taskQueue.offer(this.apply { active = false })
                } else {
                    capacity = reader.readUInt32().toInt()

                    val crcGiven: Long = ValueConverters.UINT32.deserialize(data, data.size - 4)
                    val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

                    if (crcGiven != crcCalculated) {
                        throw IllegalStateException("CRC doesn't match!")
                    }

                    initialSerialized = UTF8_STRING.deserialize(reader.readBytes(capacity), order = ByteOrder.BIG_ENDIAN)

                    try {
                        Timber.d("JSON: $initialSerialized")
                        gson.fromJson<Map<String, String>>(initialSerialized, type).entries.forEach {
                            this@ArbitraryDataImpl[it.key] = it.value
                        }
                    } catch (e: JsonSyntaxException) {
                        Timber.d("Error invalid JSON!")
                    } finally {
                        available.postValue(capacity - serialize().size)
                    }
                }
            }
        })
    }

    override operator fun set(key: String, value: String): Boolean {
        val oldValue: String? = put(key, value)
        val newAvailable: Int = capacity - serialize().size

        return if (newAvailable < 0) {
            oldValue?.let {
                put(key, oldValue)
            } ?: remove(key)
            available.value = newAvailable
            false
        } else {
            available.value = newAvailable
            true
        }
    }

    override fun removeEntry(key: String): String? {
        val value: String? = super.remove(key)
        available.value = capacity - serialize().size
        return value
    }

    // TODO Rewrite proper
    override fun write(taskProcessor: BluetoothTaskProcessor, full: Boolean) {
        gson.toJson(this).takeIf { it != initialSerialized || full }?.let { json ->
            UTF8_STRING.serialize(json, order = ByteOrder.BIG_ENDIAN)
                .copyOf(capacity)
                .let { it + UINT32.serialize(CRC32().getValueForUpdate(it)) }
                .also { value ->
                    taskProcessor.queueTask(object : CharacteristicWriteTask(
                        name = "Arbitrary Data Write Task",
                        serviceUUID = serviceUuid,
                        characteristicUUID = uuid,
                        value = value
                    ) {
                        override fun onSuccess() {
                        }
                    })
                }
        }
    }

    override fun serialize(): ByteArray {
        return UTF8_STRING.serialize(gson.toJson(this), order = ByteOrder.BIG_ENDIAN)
    }

    companion object {
        val DEFAULT_ARBITRARY_DATA_SERVICE_UUID = UUIDProvider.provideFullUUID("E000")
        val DEFAULT_ARBITRARY_DATA_CHARACTERISTIC_UUID = UUIDProvider.provideFullUUID("E001")
    }
}