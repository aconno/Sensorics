package com.aconno.sensorics.device.beacon.protobuffers.parameters


import com.aconno.sensorics.device.beacon.Parameter
import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel.*
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import java.util.zip.CRC32


class ParametersProtobufImpl : Parameters() {
    override var count: Int = 0
    override lateinit var config: Config

    private lateinit var parametersProtobufModel : ParametersProtobufModel.Parameters

    @Suppress("UNCHECKED_CAST")
    override fun fromBytes(data: ByteArray) {
        val crcGiven: Long = ValueConverters.UINT32.deserialize(data, data.size - 4)
        val crcCalculated: Long = CRC32().getValueForUpdate(data.copyOf(data.size - 4))

        if (crcGiven != crcCalculated) {
            throw IllegalStateException("CRC doesn't match!")
        }

        //parse all bytes except last 4 bytes that represent CRC
        parametersProtobufModel = ParametersProtobufModel.Parameters.parseFrom(data.sliceArray(
            IntRange(0,data.size - 5)
        ))
        parametersProtobufModel.config.let {
            config = Config(
                it.nameSize,
                it.unitSize,
                it.maxValueSize
            )
        }

        val paramMapper = ParameterProtobufMapper(
            config,
            object : ParameterProtobufMapper.IdGenerator {
                var counter = 0

                override fun generateId() : Int {
                    counter++
                    return counter
                }
            }
        )
        parametersProtobufModel.groupsMap.entries.forEach { entry ->
            val group = entry.value
            this[entry.key] =
                paramMapper.let { m ->
                    mutableListOf(
                        group.booleanParameterList.map {
                            m.mapBooleanProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.int8ParameterList.map {
                            m.mapInt8ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.int16ParameterList.map {
                            m.mapInt16ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.int32ParameterList.map {
                            m.mapInt32ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.uint8ParameterList.map {
                            m.mapUInt8ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.uint16ParameterList.map {
                            m.mapUInt16ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.uint32ParameterList.map {
                            m.mapUInt32ProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.floatParameterList.map {
                            m.mapFloatProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.enumParameterList.map {
                            m.mapEnumProtobufModelToParameter(it) as Parameter<Any>
                        },
                        group.stringParameterList.map {
                            m.mapStringProtobufModelToParameter(it) as Parameter<Any>
                        }
                    ).flatten().toMutableList()
                }

        }

    }

    //this method serializes Parameters protobuf message but leaving Config message as null since config is not supposed to change
    override fun toBytes(): ByteArray {
        updateProtobufModel()
        return parametersProtobufModel.toByteArray()
    }

    //this method does not update config attributes since they are not supposed to change
    private fun updateProtobufModel() {
        val modelBuilder = ParametersProtobufModel.Parameters.newBuilder()

        entries.forEach {entry ->
            val groupBuilder = ParameterGroup.newBuilder()
            entry.value.forEach {
                when((it as BaseParameterProtobufImpl).paramType) {
                    ParameterType.BOOLEAN -> groupBuilder.addBooleanParameter(it.toProtobufModel() as BooleanParameter)
                    ParameterType.UINT8 -> groupBuilder.addUint8Parameter(it.toProtobufModel() as UIntParameter)
                    ParameterType.UINT16 -> groupBuilder.addUint16Parameter(it.toProtobufModel() as UIntParameter)
                    ParameterType.UINT32 -> groupBuilder.addUint32Parameter(it.toProtobufModel() as UIntParameter)
                    ParameterType.INT8 -> groupBuilder.addInt8Parameter(it.toProtobufModel() as IntParameter)
                    ParameterType.INT16 -> groupBuilder.addInt16Parameter(it.toProtobufModel() as IntParameter)
                    ParameterType.INT32 -> groupBuilder.addInt32Parameter(it.toProtobufModel() as IntParameter)
                    ParameterType.FLOAT -> groupBuilder.addFloatParameter(it.toProtobufModel() as FloatParameter)
                    ParameterType.ENUM -> groupBuilder.addEnumParameter(it.toProtobufModel() as EnumParameter)
                    ParameterType.STRING -> groupBuilder.addStringParameter(it.toProtobufModel() as StringParameter)
                }
            }

            modelBuilder.putGroups(entry.key,groupBuilder.build())

        }

        parametersProtobufModel = modelBuilder.build()

    }

}