package com.aconno.sensorics.device.beacon

import com.aconno.sensorics.domain.migrate.asArrayOrNull
import com.aconno.sensorics.domain.migrate.asObjectOrNull
import com.aconno.sensorics.domain.migrate.getObjectOrNull
import com.aconno.sensorics.domain.migrate.getStringOrNull
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import timber.log.Timber

abstract class Parameters : LinkedHashMap<String, MutableList<Parameter<Any>>>() {
    abstract var count: Int
    abstract var config: Config

    fun getParameterAsString(name: String): String = getParameterValue(name, "Unavailable")

    inline fun <reified T> getParameterValue(name: String, default: T): T = flatMap {
        it.value
    }.find {
        it.name == name
    }?.getValue() as T ?: (default)

    inline fun <reified T> getParameterByName(name: String): T? = flatMap {
        it.value
    }.find { it.name == name } as T?

    /**
     * Deserializes bytes and builds the hashmap
     *
     * @param data
     */
    abstract fun fromBytes(data: ByteArray)

    /**
     * Serializes and concatenates all parameter values into a ByteArray
     *
     * @return Serialized and concatenated parameter values
     */
    abstract fun toBytes(): ByteArray


    fun toJson(): JsonObject {
        return JsonObject().apply {
            this.add("config", JsonObject().apply {
                this.addProperty("maxValueSize", config.MAX_VALUE_SIZE)
                this.addProperty("nameSize", config.NAME_SIZE)
                this.addProperty("unitSize", config.UNIT_SIZE)
            })
            this.add("parameters", JsonObject().apply {
                forEach { entry ->
                    Timber.d("Keys: ${entry.key}")
                    this.add(entry.key, JsonArray().apply {
                        entry.value.forEach {
                            this.add(it.toJson())
                        }
                    })
                }
            })
        }
    }

    @Throws(IllegalArgumentException::class)
    fun loadChangesFromJson(obj: JsonObject) {
        obj.getObjectOrNull("parameters")?.entrySet()?.forEach {
            val groupName = it.key
                ?: throw IllegalArgumentException(
                    "Group key missing in parameters entry!"
                )

            val groupParameters = it.value?.asArrayOrNull()
                ?: throw IllegalArgumentException(
                    "List missing as value in parameters entry for key $groupName!"
                )

            val realParameterGroup = this@Parameters[groupName]
                ?: throw IllegalArgumentException(
                    "Parameter group $groupName does not actually exist!"
                )

            groupParameters.forEach { groupParameterElement ->
                val groupParameter = groupParameterElement?.asObjectOrNull()
                    ?: throw IllegalArgumentException(
                        "Parameter entry in group $groupName group is not an object!"
                    )

                val groupParameterName = groupParameter.getStringOrNull("name")
                    ?: throw IllegalArgumentException(
                        "Parameter entry in group $groupName is missing name string!"
                    )

                val realParameter = realParameterGroup.find { parameter ->
                    parameter.name == groupParameterName
                }
                    ?: throw IllegalArgumentException(
                        "Parameter for $groupName -> $groupParameterName does not actually exist!"
                    )

                realParameter.loadChangesFromJson(groupParameter)
            }
        } ?: throw IllegalArgumentException(
            "Parameters attribute missing in parameters JSON object!"
        )
    }

    class Config(
        val NAME_SIZE: Int,
        val UNIT_SIZE: Int,
        val MAX_VALUE_SIZE: Int
    )
}