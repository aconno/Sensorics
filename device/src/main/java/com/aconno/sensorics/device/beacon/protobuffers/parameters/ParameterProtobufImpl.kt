package com.aconno.sensorics.device.beacon.protobuffers.parameters

import com.aconno.sensorics.device.beacon.Parameter
import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import com.aconno.sensorics.device.beacon.v2.parameters.getAsGivenTypeOrNull
import com.aconno.sensorics.domain.migrate.getBooleanOrNull
import com.aconno.sensorics.domain.migrate.getNumberOrNull
import com.aconno.sensorics.domain.migrate.getStringOrNull
import com.google.gson.JsonArray
import com.google.gson.JsonObject

enum class ParameterType(val typeId : Int) {
    BOOLEAN(0),
    UINT8(1),
    UINT16(2),
    UINT32(3),
    INT8(4),
    INT16(5),
    INT32(6),
    FLOAT(7),
    ENUM(8),
    STRING(9)
}

abstract class BaseParameterProtobufImpl<T>(
    override val id : Int,
    val paramType: ParameterType,
    val config : Parameters.Config,
    commonParameterAttributes: ParametersProtobufModel.CommonParameterAttributes
): Parameter<T>(){
    override val writable: Boolean = commonParameterAttributes.writable

    override val eventable: Boolean = commonParameterAttributes.eventable

    override val cacheSupported: Boolean = commonParameterAttributes.cacheSupported

    override var cacheEnabled: Boolean = commonParameterAttributes.cacheEnabled

    override val name: String = commonParameterAttributes.name

    override val unit: String
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")

    final override var valueInternal: T? = null

    override val choices: List<String>
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")
    override val min: Int
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")
    override val max: Int
        get() = throw NotImplementedError("Not implemented in ${this::class.java}")


    override fun toJson(): JsonObject {
        return JsonObject().apply {
            addProperty("type", paramType.typeId)
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

    abstract fun toProtobufModel() : Any

    //this method sets only attributes that are writable!
    protected fun buildCommonAtrributesProtobufModel() : ParametersProtobufModel.CommonParameterAttributes {
        return ParametersProtobufModel.CommonParameterAttributes.newBuilder()
            .setCacheEnabled(cacheEnabled).build()
    }

}

class BooleanParamProtobufImpl(
    override val id : Int,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.BooleanParameter
) : BaseParameterProtobufImpl<Boolean>(id,ParameterType.BOOLEAN,config,protobufModel.commonAttributes){

    init {
        valueInternal = protobufModel.value
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

    override fun toProtobufModel() : ParametersProtobufModel.BooleanParameter {
        return ParametersProtobufModel.BooleanParameter.newBuilder().
                setCommonAttributes(buildCommonAtrributesProtobufModel())
            .setValue(valueInternal ?: false)
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
    }

}

abstract class NumberParameterProtobufImpl<T : Number>(
    id : Int, paramType: ParameterType,
    config : Parameters.Config,
    private val numberClass: Class<T>,
    commonParameterAttributes: ParametersProtobufModel.CommonParameterAttributes,
    numberSettings : ParametersProtobufModel.NumberParameterSettings,
    parameterValue : T?
) : BaseParameterProtobufImpl<T>(id,paramType,config,commonParameterAttributes) {
    final override val min: Int
    final override val max: Int
    final override val unit: String

    init {
        valueInternal = parameterValue
        unit = numberSettings.unit
        min = numberSettings.min
        max = numberSettings.max
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

class UIntParamProtobufImpl(
    id : Int,
    paramType: ParameterType,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.UIntParameter
) :
    NumberParameterProtobufImpl<Long>(id,paramType,config,Long::class.java,
        protobufModel.commonAttributes,protobufModel.settings,

        //this conversion is necessary because proto compiler translates uint to java signed int type,
        //using the sign bit as top bit -> so this conversion makes sure that very large values are not
        //misinterpreted as negative
        convertToUnsignedValue(protobufModel.value)
        ) {

    override fun toProtobufModel() : ParametersProtobufModel.UIntParameter {
        return ParametersProtobufModel.UIntParameter.newBuilder().
            setCommonAttributes(buildCommonAtrributesProtobufModel())
            .also {
                valueInternal?.let { value ->
                    it.setValue(value.toInt())
                }
            }
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
    }

    companion object {

        private fun convertToUnsignedValue(value : Int) : Long {
            if(value >= 0) {
                return value.toLong()
            }
            val valueWithoutSignBit = (value shl 1) shr 1 //sets the sign bit to 0
            val longValue = valueWithoutSignBit.toLong()
            return longValue or 0x80000000 //sets the top bit (top bit of int value) using this mask
        }
    }

}

class IntParamProtobufImpl(
    id : Int,
    paramType: ParameterType,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.IntParameter
) :
    NumberParameterProtobufImpl<Int>(id,paramType,config,Int::class.java,
        protobufModel.commonAttributes,protobufModel.settings,
        protobufModel.value
    ) {

    override fun toProtobufModel(): ParametersProtobufModel.IntParameter {
        return ParametersProtobufModel.IntParameter.newBuilder()
            .setCommonAttributes(buildCommonAtrributesProtobufModel())
            .also {
                valueInternal?.let { value ->
                    it.setValue(value)
                }
            }
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
    }
}


class FloatParamProtobufImpl(
    id : Int,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.FloatParameter
) :
    NumberParameterProtobufImpl<Float>(id,ParameterType.FLOAT,config,Float::class.java,
        protobufModel.commonAttributes,protobufModel.settings,
        protobufModel.value
    ) {

    override fun toProtobufModel() : ParametersProtobufModel.FloatParameter {
        return ParametersProtobufModel.FloatParameter.newBuilder()
            .setCommonAttributes(buildCommonAtrributesProtobufModel())
            .also {
                valueInternal?.let { value ->
                    it.setValue(value)
                }
            }
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
    }

}

class EnumParamProtobufImpl (
    override val id : Int,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.EnumParameter
) : BaseParameterProtobufImpl<Long>(id,ParameterType.ENUM,config,protobufModel.commonAttributes){
    override val unit: String
    override val choices: List<String>

    init {
        valueInternal = protobufModel.value.toLong()
        unit = protobufModel.unit
        choices = protobufModel.choiceList
    }

    override fun toProtobufModel() : ParametersProtobufModel.EnumParameter {
        return ParametersProtobufModel.EnumParameter.newBuilder()
            .setCommonAttributes(buildCommonAtrributesProtobufModel())
            .also {
                valueInternal?.let { value ->
                    it.setValue(value.toInt())
                }
            }
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
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

class StringParamProtobufImpl(
    id : Int,
    config : Parameters.Config,
    protobufModel: ParametersProtobufModel.StringParameter
) : BaseParameterProtobufImpl<String>(id,ParameterType.STRING,config,protobufModel.commonAttributes){

    init {
        valueInternal = protobufModel.value
    }

    override fun toProtobufModel() : ParametersProtobufModel.StringParameter {
        return ParametersProtobufModel.StringParameter.newBuilder()
            .setCommonAttributes(buildCommonAtrributesProtobufModel())
            .also {
                valueInternal?.let { value ->
                    it.setValue(value)
                }
            }
            .build()
    }

    override fun toBytes(): ByteArray {
        return toProtobufModel().toByteArray()
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

class ParameterProtobufMapper(
    private val config : Parameters.Config,
    private val idGenerator: IdGenerator
) {

    interface IdGenerator {
        fun generateId() : Int
    }

    fun mapBooleanProtobufModelToParameter(protobufModel : ParametersProtobufModel.BooleanParameter) : Parameter<Boolean> {
        return BooleanParamProtobufImpl(idGenerator.generateId(),config,protobufModel)
    }

    fun mapInt8ProtobufModelToParameter(protobufModel: ParametersProtobufModel.IntParameter) : Parameter<Int> {
        return IntParamProtobufImpl(idGenerator.generateId(),ParameterType.INT8,config,protobufModel)
    }

    fun mapInt16ProtobufModelToParameter(protobufModel: ParametersProtobufModel.IntParameter) : Parameter<Int> {
        return IntParamProtobufImpl(idGenerator.generateId(),ParameterType.INT16,config,protobufModel)
    }

    fun mapInt32ProtobufModelToParameter(protobufModel: ParametersProtobufModel.IntParameter) : Parameter<Int> {
        return IntParamProtobufImpl(idGenerator.generateId(),ParameterType.INT32,config,protobufModel)
    }

    fun mapUInt8ProtobufModelToParameter(protobufModel: ParametersProtobufModel.UIntParameter) : Parameter<Long> {
        return UIntParamProtobufImpl(idGenerator.generateId(),ParameterType.UINT8,config,protobufModel)
    }

    fun mapUInt16ProtobufModelToParameter(protobufModel: ParametersProtobufModel.UIntParameter) : Parameter<Long> {
        return UIntParamProtobufImpl(idGenerator.generateId(),ParameterType.UINT16,config,protobufModel)
    }

    fun mapUInt32ProtobufModelToParameter(protobufModel: ParametersProtobufModel.UIntParameter) : Parameter<Long> {
        return UIntParamProtobufImpl(idGenerator.generateId(),ParameterType.UINT32,config,protobufModel)
    }

    fun mapFloatProtobufModelToParameter(protobufModel: ParametersProtobufModel.FloatParameter) : Parameter<Float> {
        return FloatParamProtobufImpl(idGenerator.generateId(),config,protobufModel)
    }

    fun mapEnumProtobufModelToParameter(protobufModel: ParametersProtobufModel.EnumParameter) : Parameter<Long>{
        return EnumParamProtobufImpl(idGenerator.generateId(),config,protobufModel)
    }

    fun mapStringProtobufModelToParameter(protobufModel: ParametersProtobufModel.StringParameter) : Parameter<String> {
        return StringParamProtobufImpl(idGenerator.generateId(),config,protobufModel)
    }

}