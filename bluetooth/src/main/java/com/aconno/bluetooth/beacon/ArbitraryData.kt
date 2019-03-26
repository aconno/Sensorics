package com.aconno.bluetooth.beacon

import com.aconno.bluetooth.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Type
import java.nio.ByteOrder
import java.util.*

val DEFAULT_ARBITRARY_DATA_UUID = UUIDProvider.provideFullUUID("E001")

class ArbitraryData(
        var size: Int = 0,
        var uuid: UUID = DEFAULT_ARBITRARY_DATA_UUID
) : BleReadableWritable {
    val gson: Gson = GsonBuilder().create()
    val type: Type = object : TypeToken<Map<String, String>>() {}.type


    var map: MutableMap<String, String> = mutableMapOf()
    var chunkSize: Int = 0
    var serialized: String = ""

    override fun read(): List<Task> {
        var data: ByteArray = byteArrayOf()
        return listOf(object : CharacteristicReadTask(characteristicUUID = uuid) {
            override fun onSuccess(value: ByteArray) {
                data += value
                if (data.size < size) taskQueue.offer(this.apply { active = false })
                else {
                    chunkSize = value.size
                    serialized = ValueConverter.UTF8STRING.converter.deserialize(data.copyOf(size), ByteOrder.BIG_ENDIAN) as String
                    map = try {
                        gson.fromJson(serialized, type) ?: mutableMapOf()
                    } catch (e: JsonSyntaxException) {
                        mutableMapOf()
                    }
                }
            }
        })
    }

    override fun write(full: Boolean): List<Task> {
        return gson.toJson(map).let { json ->
            if (json != serialized || full) {
                json.toByteArray().copyOf(size).chunk(chunkSize).mapIndexed { i, data ->
                    object : CharacteristicWriteTask(characteristicUUID = uuid, value = data) {
                        override fun onSuccess() {
                            Timber.i("Wrote ${i * chunkSize}/$size bytes for arbitrary data")
                        }
                    }
                }.toList()
            } else listOf()
        }
    }
}