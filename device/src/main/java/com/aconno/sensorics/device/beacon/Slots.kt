package com.aconno.sensorics.device.beacon

import com.aconno.sensorics.device.beacon.Slot.Type
import com.aconno.sensorics.domain.migrate.asObjectOrNull
import com.google.gson.JsonArray
import com.google.gson.JsonElement


abstract class Slots(
    size: Int,
    open val supportedSlots: Array<Type> = DEFAULT_SUPPORTED_SLOTS
) : ArrayList<Slot>(size) {
    abstract fun fromBytes(data: ByteArray)

    abstract fun toBytes(): ByteArray

    fun sortedEmptyLast(): List<Slot> = sortedBy { it.getType() == Type.EMPTY }


    fun toJson(): JsonElement {
        return JsonArray().apply {
            this@Slots.forEach { slot ->
                this.add(slot.toJson())
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonArray) {

        forEachIndexed { index, slot ->
            obj.get(index)?.let { slotElement ->
                slotElement.asObjectOrNull()?.let {
                    slot.loadChangesFromJson(it)
                } ?: throw IllegalArgumentException(
                    "Supplied slot at index $index should be object but is not!"
                )
            } ?: throw IllegalArgumentException(
                "Illegal amount of slots supplied, should be $size"
            )
        }
    }

    companion object {
        val DEFAULT_SUPPORTED_SLOTS: Array<Type> = "EMPTY,CUSTOM,URL,I_BEACON,DEFAULT"
            .split(',')
            .map { Type.valueOf(it) }
            .toTypedArray()
    }
}