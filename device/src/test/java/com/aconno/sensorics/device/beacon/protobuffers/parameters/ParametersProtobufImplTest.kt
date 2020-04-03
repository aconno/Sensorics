package com.aconno.sensorics.device.beacon.protobuffers.parameters

import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.zip.CRC32

class ParametersProtobufImplTest {

    @Test
    fun testSimpleModelReading() {
        val paramsProtobufImpl = ParametersProtobufImpl()
        paramsProtobufImpl.fromBytes(buildTestReading(getSimpleProtobufModel()))

        assertThat(paramsProtobufImpl.config.NAME_SIZE, `is`(CONFIG_NAME_SIZE))
        assertThat(paramsProtobufImpl.config.MAX_VALUE_SIZE, `is`(CONFIG_MAX_VALUE_SIZE))
        assertThat(paramsProtobufImpl.config.UNIT_SIZE, `is`(CONFIG_UNIT_SIZE))
    }

    private fun buildTestReading(protobufModel: ParametersProtobufModel.Parameters) : ByteArray  {
        val protobufModelBytes = protobufModel.toByteArray()
        val crc = CRC32().getValueForUpdate(protobufModelBytes)

        return protobufModelBytes + ValueConverters.UINT32.serialize(crc)
    }

    private fun getSimpleProtobufModel(): ParametersProtobufModel.Parameters {
        return ParametersProtobufModel.Parameters.newBuilder()
            .setConfig(
                ParametersProtobufModel.Parameters.Config.newBuilder()
                    .setNameSize(CONFIG_NAME_SIZE)
                    .setUnitSize(CONFIG_UNIT_SIZE)
                    .setMaxValueSize(CONFIG_MAX_VALUE_SIZE)
            )

            .build()
    }

    private fun buildCommonParamAttributes(writable : Boolean,eventable : Boolean,
                                           cacheSupported : Boolean, cacheEnabled : Boolean,
                                           name : String) : ParametersProtobufModel.CommonParameterAttributes {
        return ParametersProtobufModel.CommonParameterAttributes.newBuilder()
            .setWritable(writable)
            .setEventable(eventable)
            .setCacheSupported(cacheSupported)
            .setCacheEnabled(cacheEnabled)
            .setName(name)
            .build()

    }

    private fun buildNumberParamSettings(unit : String, min : Int, max : Int) : ParametersProtobufModel.NumberParameterSettings {
        return ParametersProtobufModel.NumberParameterSettings.newBuilder()
            .setUnit(unit)
            .setMin(min)
            .setMax(max)
            .build()

    }

    private fun getFullProtobufModel(): ParametersProtobufModel.Parameters {


        return ParametersProtobufModel.Parameters.newBuilder()
            .setConfig(
                ParametersProtobufModel.Parameters.Config.newBuilder()
                    .setNameSize(CONFIG_NAME_SIZE)
                    .setUnitSize(CONFIG_UNIT_SIZE)
                    .setMaxValueSize(CONFIG_MAX_VALUE_SIZE)
            )

            .putGroups(
                PARAM_GROUP1,
                ParametersProtobufModel.ParameterGroup.newBuilder()
                    .addBooleanParameter(
                        ParametersProtobufModel.BooleanParameter.newBuilder()
                            .setValue(GROUP1_BOOL_PARAM_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP1_BOOL_PARAM_WRITABLE, GROUP1_BOOL_PARAM_EVENTABLE,
                                GROUP1_BOOL_PARAM_CACHE_SUPPORTED,
                                GROUP1_BOOL_PARAM_CACHE_ENABLED, GROUP1_BOOL_PARAM_NAME))

                    )
                    .addUint32Parameter(
                        ParametersProtobufModel.UIntParameter.newBuilder()
                            .setValue(GROUP1_UINT32_PARAM1_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP1_UINT32_PARAM1_WRITABLE, GROUP1_UINT32_PARAM1_EVENTABLE,
                                GROUP1_UINT32_PARAM1_CACHE_SUPPORTED,
                                GROUP1_UINT32_PARAM1_CACHE_ENABLED, GROUP1_UINT32_PARAM1_NAME))
                            .setSettings(buildNumberParamSettings(GROUP1_UINT32_PARAM1_UNIT,
                                GROUP1_UINT32_PARAM1_MIN, GROUP1_UINT32_PARAM1_MAX))

                    )
                    .addUint32Parameter(
                        ParametersProtobufModel.UIntParameter.newBuilder()
                            .setValue(GROUP1_UINT32_PARAM2_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP1_UINT32_PARAM2_WRITABLE, GROUP1_UINT32_PARAM2_EVENTABLE,
                                GROUP1_UINT32_PARAM2_CACHE_SUPPORTED,
                                GROUP1_UINT32_PARAM2_CACHE_ENABLED, GROUP1_UINT32_PARAM2_NAME))
                            .setSettings(buildNumberParamSettings(GROUP1_UINT32_PARAM2_UNIT,
                                GROUP1_UINT32_PARAM2_MIN, GROUP1_UINT32_PARAM2_MAX))

                    )

                    .build()


            )

            .putGroups(
                PARAM_GROUP2,
                ParametersProtobufModel.ParameterGroup.newBuilder()
                    .addInt16Parameter(
                        ParametersProtobufModel.IntParameter.newBuilder()
                                .setValue(GROUP2_INT16_PARAM_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP2_INT16_PARAM_WRITABLE, GROUP2_INT16_PARAM_EVENTABLE,
                                GROUP2_INT16_PARAM_CACHE_SUPPORTED,
                                GROUP2_INT16_PARAM_CACHE_ENABLED, GROUP2_INT16_PARAM_NAME))
                            .setSettings(buildNumberParamSettings(GROUP2_INT16_PARAM_UNIT,
                                GROUP2_INT16_PARAM_MIN, GROUP2_INT16_PARAM_MAX))

                    )
                    .addFloatParameter(
                        ParametersProtobufModel.FloatParameter.newBuilder()
                            .setValue(GROUP2_FLOAT_PARAM_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP2_FLOAT_PARAM_WRITABLE, GROUP2_FLOAT_PARAM_EVENTABLE,
                                GROUP2_FLOAT_PARAM_CACHE_SUPPORTED,
                                GROUP2_FLOAT_PARAM_CACHE_ENABLED, GROUP2_FLOAT_PARAM_NAME))
                            .setSettings(buildNumberParamSettings(GROUP2_FLOAT_PARAM_UNIT,
                                GROUP2_FLOAT_PARAM_MIN, GROUP2_FLOAT_PARAM_MAX))

                    )
                    .addStringParameter(
                        ParametersProtobufModel.StringParameter.newBuilder()
                            .setValue(GROUP2_STRING_PARAM_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP2_STRING_PARAM_WRITABLE, GROUP2_STRING_PARAM_EVENTABLE,
                                GROUP2_STRING_PARAM_CACHE_SUPPORTED,
                                GROUP2_STRING_PARAM_CACHE_ENABLED, GROUP2_STRING_PARAM_NAME))

                    )
                    .addEnumParameter(
                        ParametersProtobufModel.EnumParameter.newBuilder()
                            .setValue(GROUP2_ENUM_PARAM_VALUE)
                            .setCommonAttributes(buildCommonParamAttributes(
                                GROUP2_ENUM_PARAM_WRITABLE, GROUP2_ENUM_PARAM_EVENTABLE,
                                GROUP2_ENUM_PARAM_CACHE_SUPPORTED,
                                GROUP2_ENUM_PARAM_CACHE_ENABLED, GROUP2_ENUM_PARAM_NAME))
                            .setUnit(GROUP2_ENUM_PARAM_UNIT)
                            .addChoice(GROUP2_ENUM_PARAM_CHOICE1)
                            .addChoice(GROUP2_ENUM_PARAM_CHOICE2)
                            .addChoice(GROUP2_ENUM_PARAM_CHOICE3)
                    )

                    .build()


            )


            .build()
    }


    companion object {
        const val CONFIG_NAME_SIZE = 100
        const val CONFIG_UNIT_SIZE = 100
        const val CONFIG_MAX_VALUE_SIZE = 100

        const val PARAM_GROUP1 = "group1"
        const val PARAM_GROUP2 = "group2"

        const val GROUP1_BOOL_PARAM_WRITABLE = false
        const val GROUP1_BOOL_PARAM_EVENTABLE = false
        const val GROUP1_BOOL_PARAM_CACHE_SUPPORTED = true
        const val GROUP1_BOOL_PARAM_CACHE_ENABLED = true
        const val GROUP1_BOOL_PARAM_NAME = "boolParam1"
        const val GROUP1_BOOL_PARAM_VALUE = true

        const val GROUP1_UINT32_PARAM1_WRITABLE = true
        const val GROUP1_UINT32_PARAM1_EVENTABLE = false
        const val GROUP1_UINT32_PARAM1_CACHE_SUPPORTED = false
        const val GROUP1_UINT32_PARAM1_CACHE_ENABLED = true
        const val GROUP1_UINT32_PARAM1_NAME = "uint32Param1"
        const val GROUP1_UINT32_PARAM1_VALUE = 188
        const val GROUP1_UINT32_PARAM1_UNIT = "unit1"
        const val GROUP1_UINT32_PARAM1_MIN = 15
        const val GROUP1_UINT32_PARAM1_MAX = 155

        const val GROUP1_UINT32_PARAM2_WRITABLE = false
        const val GROUP1_UINT32_PARAM2_EVENTABLE = true
        const val GROUP1_UINT32_PARAM2_CACHE_SUPPORTED = true
        const val GROUP1_UINT32_PARAM2_CACHE_ENABLED = true
        const val GROUP1_UINT32_PARAM2_NAME = "uint32Param2"
        const val GROUP1_UINT32_PARAM2_VALUE = 355
        const val GROUP1_UINT32_PARAM2_UNIT = "unit2"
        const val GROUP1_UINT32_PARAM2_MIN = 33
        const val GROUP1_UINT32_PARAM2_MAX = 777

        const val GROUP2_INT16_PARAM_WRITABLE = true
        const val GROUP2_INT16_PARAM_EVENTABLE = true
        const val GROUP2_INT16_PARAM_CACHE_SUPPORTED = true
        const val GROUP2_INT16_PARAM_CACHE_ENABLED = true
        const val GROUP2_INT16_PARAM_NAME = "int16Param"
        const val GROUP2_INT16_PARAM_VALUE = 22
        const val GROUP2_INT16_PARAM_UNIT = "unit3"
        const val GROUP2_INT16_PARAM_MIN = 11
        const val GROUP2_INT16_PARAM_MAX = 432

        const val GROUP2_FLOAT_PARAM_WRITABLE = false
        const val GROUP2_FLOAT_PARAM_EVENTABLE = false
        const val GROUP2_FLOAT_PARAM_CACHE_SUPPORTED = true
        const val GROUP2_FLOAT_PARAM_CACHE_ENABLED = true
        const val GROUP2_FLOAT_PARAM_NAME = "floatParam"
        const val GROUP2_FLOAT_PARAM_VALUE = 34.54f
        const val GROUP2_FLOAT_PARAM_UNIT = "unit4"
        const val GROUP2_FLOAT_PARAM_MIN = 110
        const val GROUP2_FLOAT_PARAM_MAX = 567

        const val GROUP2_STRING_PARAM_WRITABLE = true
        const val GROUP2_STRING_PARAM_EVENTABLE = true
        const val GROUP2_STRING_PARAM_CACHE_SUPPORTED = false
        const val GROUP2_STRING_PARAM_CACHE_ENABLED = false
        const val GROUP2_STRING_PARAM_NAME = "stringParam1"
        const val GROUP2_STRING_PARAM_VALUE = "sampleStringValue"

        const val GROUP2_ENUM_PARAM_WRITABLE = true
        const val GROUP2_ENUM_PARAM_EVENTABLE = true
        const val GROUP2_ENUM_PARAM_CACHE_SUPPORTED = false
        const val GROUP2_ENUM_PARAM_CACHE_ENABLED = false
        const val GROUP2_ENUM_PARAM_NAME = "enumParam1"
        const val GROUP2_ENUM_PARAM_VALUE = 2
        const val GROUP2_ENUM_PARAM_UNIT = "unit5"
        const val GROUP2_ENUM_PARAM_CHOICE1 = "enumChoice1"
        const val GROUP2_ENUM_PARAM_CHOICE2 = "enumChoice2"
        const val GROUP2_ENUM_PARAM_CHOICE3 = "enumChoice3"
    }
}