package com.aconno.sensorics.device.beacon

import com.aconno.sensorics.device.beacon.Slot.Type
import com.aconno.sensorics.domain.migrate.asObjectOrNull
import com.aconno.sensorics.domain.migrate.getArrayOrNull
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject


abstract class Slots(
    size: Int,
    open val supportedSlots: Array<Type> = DEFAULT_SUPPORTED_SLOTS
) : ArrayList<Slot>(size) {
    abstract var config: Config

    abstract fun fromBytes(data: ByteArray)

    abstract fun toBytes(): ByteArray

    fun sortedEmptyLast(): List<Slot> = sortedBy { it.getType() == Type.EMPTY }


    fun toJson(): JsonElement {
        return JsonObject().apply {
            this.add("config", JsonObject().apply {
                this.addProperty("nameSize", config.NAME_SIZE)
                this.addProperty("frameTypeSize", config.FRAME_TYPE_SIZE)
                this.addProperty("advFormatSize", config.ADV_FORMAT_SIZE)
            })
            this.add("slots", JsonArray().apply {
                this@Slots.forEach { slot ->
                    this.add(slot.toJson())
                }
            })
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonObject) {
        obj.getArrayOrNull("slots")?.let { slots ->
            forEachIndexed { index, slot ->
                slots.get(index)?.let { slotElement ->
                    slotElement.asObjectOrNull()?.let {
                        slot.loadChangesFromJson(it)
                    } ?: throw IllegalArgumentException(
                        "Supplied slot at index $index should be object but is not!"
                    )
                } ?: throw IllegalArgumentException(
                    "Illegal amount of slots supplied, should be $size"
                )
            }
        } ?: throw IllegalArgumentException(
            "Slots attribute missing in slots JSON object!"
        )
    }

    companion object {
        val DEFAULT_SUPPORTED_SLOTS: Array<Type> = "EMPTY,CUSTOM,URL,I_BEACON,DEFAULT"
            .split(',')
            .map { Type.valueOf(it) }
            .toTypedArray()
    }

    class Config(
        val NAME_SIZE: Int,
        val FRAME_TYPE_SIZE: Int,
        val ADV_FORMAT_SIZE: Int
    )
}