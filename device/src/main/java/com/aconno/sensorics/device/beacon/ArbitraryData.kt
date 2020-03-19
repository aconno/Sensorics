package com.aconno.sensorics.device.beacon

import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

abstract class ArbitraryData(open var capacity: Int = 0) : LinkedHashMap<String, String>() {
    abstract val dirty: Boolean
    val available: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also { it.postValue(0) }
    }

    abstract fun serialize(): ByteArray
    abstract fun set(key: String, value: String): Boolean
    abstract fun setAll(newMap: Map<String, String>): Boolean

    abstract fun removeEntry(key: String): String?

    fun toJson(): JsonElement {
        return JsonObject().apply {
            addProperty("available", available.value)
            add("arbitraryDataEntries", gson.toJsonTree(this@ArbitraryData, typeToken))
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonObject) {
        try {
            gson.fromJson<Map<String, String>>(obj.get("arbitraryDataEntries"), typeToken)
                .let {
                    this.setAll(it)
                }
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Invalid ArbitraryData format, should be $typeToken!", e
            )
        }
    }

    companion object {
        private val gson = GsonBuilder().create()
        private val typeToken = object : TypeToken<Map<String, String>>() {}.type
    }
}