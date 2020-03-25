package com.aconno.sensorics.device.beacon

import com.google.gson.JsonObject

abstract class Parameter<T> {
    abstract val id: Int

    var dirty: Boolean = false

    abstract val writable: Boolean

    abstract val eventable: Boolean

    abstract val cacheSupported: Boolean

    abstract var cacheEnabled: Boolean

    abstract val name: String

    abstract val unit: String

    abstract val choices: List<String>

    protected abstract var valueInternal: T?

    abstract val min: Int

    abstract val max: Int

    fun getValue(): T {
        return valueInternal ?: throw IllegalStateException(
            "Value was not initialized in the constructor!"
        )
    }

    fun setValue(value: T) {
        if (valueInternal != value) {
            valueInternal = value
            dirty = true
        }
    }

    /**
     * Convert parameter value to its byte writable representation
     *
     * @return
     */
    abstract fun toBytes(): ByteArray

    abstract fun toJson(): JsonObject

    @Throws(IllegalArgumentException::class)
    abstract fun loadChangesFromJson(obj: JsonObject)

    override fun toString(): String {
        return "$id - $name"
    }
}