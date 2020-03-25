package com.aconno.sensorics.device.beacon.protobuffers.parameters

import com.aconno.sensorics.device.beacon.Parameter
import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import com.aconno.sensorics.domain.migrate.getBooleanOrNull
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

abstract class UInt8ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.UIntParameter

}

abstract class UInt16ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.UIntParameter

}

abstract class UInt32ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.UIntParameter

}

abstract class Int8ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.IntParameter

}

abstract class Int16ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.IntParameter

}

abstract class Int32ParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.IntParameter

}


abstract class FloatParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.FloatParameter

}

abstract class EnumParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.EnumParameter

}

abstract class StringParamProtobufImpl : Parameter<Any>(){

    abstract fun toProtobufModel() : ParametersProtobufModel.StringParameter

}

object ParameterProtobufMapper {

    fun mapBooleanProtobufModelToParameter(booleanParamProtobufModel : ParametersProtobufModel.BooleanParameter) : Parameter<Any> {
        TODO()
    }

    fun mapInt32ProtobufModelToParameter(int32ParamProtobufModel: ParametersProtobufModel.IntParameter) : Parameter<Any> {
        TODO()
    }

    fun mapUInt32ProtobufModelToParameter(uint32ParamProtobufModel: ParametersProtobufModel.UIntParameter) : Parameter<Any> {
        TODO()
    }

    fun mapFloatProtobufModelToParameter(floatParamProtobufModel: ParametersProtobufModel.FloatParameter) : Parameter<Any> {
        TODO()
    }

    fun mapEnumProtobufModelToParameter(enumParamProtobufModel: ParametersProtobufModel.EnumParameter) : Parameter<Any>{
        TODO()
    }

    fun mapStringProtobufModelToParameter(stringParamProtobufModel: ParametersProtobufModel.StringParameter) : Parameter<Any> {
        TODO()
    }

}