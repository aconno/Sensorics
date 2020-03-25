package com.aconno.sensorics.device.beacon.v2.parameters

import com.aconno.sensorics.device.beacon.Parameter
import com.aconno.sensorics.device.beacon.Parameters.Config
import com.aconno.sensorics.domain.migrate.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import timber.log.Timber

abstract class BaseParameterImpl<T>(
    override val id: Int,
    @Suppress("CanBeParameter") val typeId: Int,
    val reader: ValueReader,
    val config: Config
) : Parameter<T>() {
    val type: ValueConverterBase<T> = ValueConverters.Factory.createFromTypeId(typeId)

    @Suppress("JoinDeclarationAndAssignment")
    private var flags: Int

    final override val writable: Boolean

    final override val eventable: Boolean

    final override val cacheSupported: Boolean

    final override var cacheEnabled: Boolean
        get() = flags extractFlag 8
        set(value) {
            flags = if (value) flags setFlag 8
            else flags clearFlag 8
        }

    final override val name: String

    override val unit: String
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")

    protected val valueData: ByteArray

    final override var valueInternal: T? = null

    override val choices: List<String>
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")
    override val min: Int
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")
    override val max: Int
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")


    constructor(id: Int, typeId: Int, data: ByteArray, config: Config)
            : this(id, typeId, ValueReaderImpl(data), config)

    init {
        flags = reader.readUInt16()
        writable = !(flags extractFlag 0)
        eventable = flags extractFlag 1
        cacheSupported = flags extractFlag 2
        name = reader.readAsciiString(config.NAME_SIZE)
        valueData = reader.readBytes(config.MAX_VALUE_SIZE)
    }

    override fun toBytes(): ByteArray {
        var data: ByteArray = type.serialize(getValue()).let {
            if (type == ValueConverters.ASCII_STRING) it.reversedArray().copyOf(config.MAX_VALUE_SIZE) else it
        }
        data = ValueConverters.UINT16.serialize(flags) + data
        Timber.d("$name -> ${getValue()} -> ${data.toHex()}")
        return data
    }

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty("type", typeId)
            addProperty("writable", writable)
            addProperty("eventable", eventable)
            addProperty("cacheSupported", cacheSupported)
            addProperty("cacheEnabled", cacheEnabled)
            addProperty("name", name)
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun loadChangesFromJson(obj: JsonObject) {
        val cacheEnabled = obj.getBooleanOrNull("cacheEnabled")
            ?: throw IllegalArgumentException(
                "CacheEnabled variable does not exist or is not a boolean!"
            )

        this.cacheEnabled = cacheEnabled
    }

    class Factory {
        companion object {
            fun <T> create(id: Int, reader: ValueReader, config: Config): Parameter<T> {
                @Suppress("UNCHECKED_CAST")
                reader.readUInt16().let { typeId ->
                    return (when (typeId) {
                        0 -> BooleanParameterImpl(id, typeId, reader, config)
                        1 -> UInt8ParameterImpl(id, typeId, reader, config)
                        2 -> UInt16ParameterImpl(id, typeId, reader, config)
                        3 -> UInt32ParameterImpl(id, typeId, reader, config)
                        4 -> Int8ParameterImpl(id, typeId, reader, config)
                        5 -> Int16ParameterImpl(id, typeId, reader, config)
                        6 -> Int32ParameterImpl(id, typeId, reader, config)
                        7 -> FloatParameterImpl(id, typeId, reader, config)
                        8 -> EnumParameterImpl(id, typeId, reader, config)
                        9 -> StringParameterImpl(id, typeId, reader, config)
                        else -> throw NotImplementedError("Unimplemented parameter type")
                    } as? Parameter<T>)?.also {
                        // TODO: Find a way to kill this in compile time
                        val nullcheck = it.getValue()
                    } ?: throw IllegalStateException("Invalid entry in factory switch statement")
                }
            }
        }
    }
}

class BooleanParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    BaseParameterImpl<Boolean>(id, typeId, reader, config) {
    init {
        val valueReader = ValueReaderImpl(valueData)
        valueInternal = valueReader.readBoolean()
    }

    override fun toJson(): JsonObject {
        return super.toJson().apply {
            addProperty("value", getValue())
        }
    }

    override fun loadChangesFromJson(obj: JsonObject) {
        super.loadChangesFromJson(obj)

        val value = obj.getBooleanOrNull("value")
            ?: throw IllegalArgumentException(
                "Value variable does not exist or is not a boolean!"
            )

        setValue(value)
    }
}

open class NumberParameterImpl<T : Number>(
    id: Int, typeId: Int, reader: ValueReader, config: Config, private val numberClass: Class<T>
) : BaseParameterImpl<T>(id, typeId, reader, config) {
    final override val min: Int
    final override val max: Int
    final override val unit: String

    init {
        val valueReader = ValueReaderImpl(valueData)
        valueInternal = valueReader.read(type)
        valueReader.currentIndex += 4 - type.length
        unit = valueReader.readAsciiString(config.UNIT_SIZE)
        min = valueReader.readInt32()
        max = valueReader.readInt32()
    }

    override fun toJson(): JsonObject {
        return super.toJson().apply {
            addProperty("value", getValue())
            addProperty("unit", unit)
            addProperty("min", min)
            addProperty("max", max)
        }
    }

    override fun loadChangesFromJson(obj: JsonObject) {
        super.loadChangesFromJson(obj)

        val value = obj.getNumberOrNull("value")?.let {
            getAsGivenTypeOrNull(it.toString(), numberClass)
        } ?: throw IllegalArgumentException(
            "Value variable does not exist or is not a $numberClass!"
        )

        setValue(value)
    }
}

class Int8ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Byte>(id, typeId, reader, config, Byte::class.java)

class UInt8ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Short>(id, typeId, reader, config, Short::class.java)

class Int16ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Short>(id, typeId, reader, config, Short::class.java)

class UInt16ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Int>(id, typeId, reader, config, Int::class.java)

class Int32ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Int>(id, typeId, reader, config, Int::class.java)

class UInt32ParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Long>(id, typeId, reader, config, Long::class.java)

class FloatParameterImpl(id: Int, typeId: Int, reader: ValueReader, config: Config) :
    NumberParameterImpl<Float>(id, typeId, reader, config, Float::class.java)

class EnumParameterImpl(
    id: Int, typeId: Int, reader: ValueReader, config: Config
) : BaseParameterImpl<Long>(id, typeId, reader, config) {
    override val unit: String
    override val choices: List<String>

    init {
        val valueReader = ValueReaderImpl(valueData)
        valueInternal = valueReader.readUInt32()
        unit = valueReader.readAsciiString(config.UNIT_SIZE)
        choices = valueReader.readAsciiString(
            valueReader.data.size - valueReader.currentIndex
        ).split(',')
    }

    override fun toJson(): JsonObject {
        return super.toJson().apply {
            addProperty("value", getValue())
            addProperty("unit", unit)
            add("choices", JsonArray().apply { choices.forEach { add(it) } })
        }
    }

    override fun loadChangesFromJson(obj: JsonObject) {
        super.loadChangesFromJson(obj)

        val value = obj.getNumberOrNull("value")
            ?.toLong()
            ?: throw IllegalArgumentException(
                "Value variable does not exist or is not a long!"
            )

        setValue(value)
    }
}

class StringParameterImpl(
    id: Int, typeId: Int, reader: ValueReader, config: Config
) : BaseParameterImpl<String>(id, typeId, reader, config) {
    init {
        val valueReader = ValueReaderImpl(valueData)
        valueInternal = valueReader.readAsciiString(config.MAX_VALUE_SIZE)
    }

    override fun toJson(): JsonObject {
        return super.toJson().apply {
            addProperty("value", getValue())
        }
    }

    override fun loadChangesFromJson(obj: JsonObject) {
        super.loadChangesFromJson(obj)

        val value = obj.getStringOrNull("value")
            ?: throw IllegalArgumentException(
                "Value variable does not exist or is not a string!"
            )

        setValue(value)
    }
}