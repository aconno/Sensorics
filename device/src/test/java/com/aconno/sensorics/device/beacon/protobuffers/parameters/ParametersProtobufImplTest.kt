package com.aconno.sensorics.device.beacon.protobuffers.parameters

import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.migrate.getValueForUpdate
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.lang.IllegalStateException
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testFullModelReading() {
        val paramsProtobufImpl = ParametersProtobufImpl()
        paramsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        assertThat(paramsProtobufImpl.config.NAME_SIZE, `is`(CONFIG_NAME_SIZE))
        assertThat(paramsProtobufImpl.config.MAX_VALUE_SIZE, `is`(CONFIG_MAX_VALUE_SIZE))
        assertThat(paramsProtobufImpl.config.UNIT_SIZE, `is`(CONFIG_UNIT_SIZE))

        assertThat(paramsProtobufImpl.entries.size, `is`(2)) //2 param group
        assertThat(paramsProtobufImpl[PARAM_GROUP1]?.size, `is`(3))
        assertThat(paramsProtobufImpl[PARAM_GROUP2]?.size, `is`(4))

        val group1 = paramsProtobufImpl[PARAM_GROUP1]!!.apply { sortBy { it.id } }

        val param1 = group1[0] as BaseParameterProtobufImpl<Boolean>
        assertThat(param1.getValue(), `is`(GROUP1_BOOL_PARAM_VALUE))
        assertThat(param1.paramType, `is`(ParameterType.BOOLEAN))
        assertThat(param1.id, `is`(1))
        assertThat(param1.config, `is`(paramsProtobufImpl.config))
        assertThat(param1.writable, `is`(GROUP1_BOOL_PARAM_WRITABLE))
        assertThat(param1.eventable, `is`(GROUP1_BOOL_PARAM_EVENTABLE))
        assertThat(param1.cacheSupported, `is`(GROUP1_BOOL_PARAM_CACHE_SUPPORTED))
        assertThat(param1.cacheEnabled, `is`(GROUP1_BOOL_PARAM_CACHE_ENABLED))
        assertThat(param1.name, `is`(GROUP1_BOOL_PARAM_NAME))

        val param2 = group1[1] as BaseParameterProtobufImpl<Long>
        assertThat(param2.getValue(), `is`(GROUP1_UINT32_PARAM1_VALUE.toLong()))
        assertThat(param2.paramType, `is`(ParameterType.UINT32))
        assertThat(param2.id, `is`(2))
        assertThat(param2.config, `is`(paramsProtobufImpl.config))
        assertThat(param2.writable, `is`(GROUP1_UINT32_PARAM1_WRITABLE))
        assertThat(param2.eventable, `is`(GROUP1_UINT32_PARAM1_EVENTABLE))
        assertThat(param2.cacheSupported, `is`(GROUP1_UINT32_PARAM1_CACHE_SUPPORTED))
        assertThat(param2.cacheEnabled, `is`(GROUP1_UINT32_PARAM1_CACHE_ENABLED))
        assertThat(param2.name, `is`(GROUP1_UINT32_PARAM1_NAME))
        assertThat(param2.unit, `is`(GROUP1_UINT32_PARAM1_UNIT))
        assertThat(param2.min, `is`(GROUP1_UINT32_PARAM1_MIN))
        assertThat(param2.max, `is`(GROUP1_UINT32_PARAM1_MAX))

        val param3 = group1[2] as BaseParameterProtobufImpl<Long>
        assertThat(param3.getValue(), `is`(GROUP1_UINT32_PARAM2_VALUE.toLong()))
        assertThat(param3.paramType, `is`(ParameterType.UINT32))
        assertThat(param3.id, `is`(3))
        assertThat(param3.config, `is`(paramsProtobufImpl.config))
        assertThat(param3.writable, `is`(GROUP1_UINT32_PARAM2_WRITABLE))
        assertThat(param3.eventable, `is`(GROUP1_UINT32_PARAM2_EVENTABLE))
        assertThat(param3.cacheSupported, `is`(GROUP1_UINT32_PARAM2_CACHE_SUPPORTED))
        assertThat(param3.cacheEnabled, `is`(GROUP1_UINT32_PARAM2_CACHE_ENABLED))
        assertThat(param3.name, `is`(GROUP1_UINT32_PARAM2_NAME))
        assertThat(param3.unit, `is`(GROUP1_UINT32_PARAM2_UNIT))
        assertThat(param3.min, `is`(GROUP1_UINT32_PARAM2_MIN))
        assertThat(param3.max, `is`(GROUP1_UINT32_PARAM2_MAX))


        val group2 = paramsProtobufImpl[PARAM_GROUP2]!!.apply { sortBy { it.id } }

        val param4 = group2[0] as BaseParameterProtobufImpl<Int>
        assertThat(param4.getValue(), `is`(GROUP2_INT16_PARAM_VALUE))
        assertThat(param4.paramType, `is`(ParameterType.INT16))
        assertThat(param4.id, `is`(4))
        assertThat(param4.config, `is`(paramsProtobufImpl.config))
        assertThat(param4.writable, `is`(GROUP2_INT16_PARAM_WRITABLE))
        assertThat(param4.eventable, `is`(GROUP2_INT16_PARAM_EVENTABLE))
        assertThat(param4.cacheSupported, `is`(GROUP2_INT16_PARAM_CACHE_SUPPORTED))
        assertThat(param4.cacheEnabled, `is`(GROUP2_INT16_PARAM_CACHE_ENABLED))
        assertThat(param4.name, `is`(GROUP2_INT16_PARAM_NAME))
        assertThat(param4.unit, `is`(GROUP2_INT16_PARAM_UNIT))
        assertThat(param4.min, `is`(GROUP2_INT16_PARAM_MIN))
        assertThat(param4.max, `is`(GROUP2_INT16_PARAM_MAX))


        val param5 = group2[1] as BaseParameterProtobufImpl<Float>
        assertThat(param5.getValue(), `is`(GROUP2_FLOAT_PARAM_VALUE))
        assertThat(param5.paramType, `is`(ParameterType.FLOAT))
        assertThat(param5.id, `is`(5))
        assertThat(param5.config, `is`(paramsProtobufImpl.config))
        assertThat(param5.writable, `is`(GROUP2_FLOAT_PARAM_WRITABLE))
        assertThat(param5.eventable, `is`(GROUP2_FLOAT_PARAM_EVENTABLE))
        assertThat(param5.cacheSupported, `is`(GROUP2_FLOAT_PARAM_CACHE_SUPPORTED))
        assertThat(param5.cacheEnabled, `is`(GROUP2_FLOAT_PARAM_CACHE_ENABLED))
        assertThat(param5.name, `is`(GROUP2_FLOAT_PARAM_NAME))
        assertThat(param5.unit, `is`(GROUP2_FLOAT_PARAM_UNIT))
        assertThat(param5.min, `is`(GROUP2_FLOAT_PARAM_MIN))
        assertThat(param5.max, `is`(GROUP2_FLOAT_PARAM_MAX))


        val param6 = group2[2] as BaseParameterProtobufImpl<Long>
        assertThat(param6.getValue(), `is`(GROUP2_ENUM_PARAM_VALUE.toLong()))
        assertThat(param6.paramType, `is`(ParameterType.ENUM))
        assertThat(param6.id, `is`(6))
        assertThat(param6.config, `is`(paramsProtobufImpl.config))
        assertThat(param6.writable, `is`(GROUP2_ENUM_PARAM_WRITABLE))
        assertThat(param6.eventable, `is`(GROUP2_ENUM_PARAM_EVENTABLE))
        assertThat(param6.cacheSupported, `is`(GROUP2_ENUM_PARAM_CACHE_SUPPORTED))
        assertThat(param6.cacheEnabled, `is`(GROUP2_ENUM_PARAM_CACHE_ENABLED))
        assertThat(param6.name, `is`(GROUP2_ENUM_PARAM_NAME))
        assertThat(param6.unit, `is`(GROUP2_ENUM_PARAM_UNIT))
        assertThat(param6.choices.size, `is`(3))
        assertThat(param6.choices[0], `is`( GROUP2_ENUM_PARAM_CHOICE1))
        assertThat(param6.choices[1], `is`( GROUP2_ENUM_PARAM_CHOICE2))
        assertThat(param6.choices[2], `is`( GROUP2_ENUM_PARAM_CHOICE3))


        val param7 = group2[3] as BaseParameterProtobufImpl<String>
        assertThat(param7.getValue(), `is`(GROUP2_STRING_PARAM_VALUE))
        assertThat(param7.paramType, `is`(ParameterType.STRING))
        assertThat(param7.id, `is`(7))
        assertThat(param7.config, `is`(paramsProtobufImpl.config))
        assertThat(param7.writable, `is`(GROUP2_STRING_PARAM_WRITABLE))
        assertThat(param7.eventable, `is`(GROUP2_STRING_PARAM_EVENTABLE))
        assertThat(param7.cacheSupported, `is`(GROUP2_STRING_PARAM_CACHE_SUPPORTED))
        assertThat(param7.cacheEnabled, `is`(GROUP2_STRING_PARAM_CACHE_ENABLED))
        assertThat(param7.name, `is`(GROUP2_STRING_PARAM_NAME))
    }

    @Test(expected = IllegalStateException::class)
    fun testCorruptedReading() {
        val paramsProtobufImpl = ParametersProtobufImpl()

        val bytes = buildTestReading(getFullProtobufModel())
        bytes[10] = (bytes[10] + 5).toByte() //changing some byte to simulate corrupted reading

        paramsProtobufImpl.fromBytes(bytes)
    }

    @Test
    fun testWritingUnchangedContent() {
        val paramsProtobufImpl = ParametersProtobufImpl()
        paramsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        val bytes = paramsProtobufImpl.toBytes()
        val protobufModel = ParametersProtobufModel.Parameters.parseFrom(
            bytes.sliceArray(IntRange(0,bytes.size - 5))
        )

        assertThat(protobufModel.groupsCount, `is`(2)) //2 param group

        val group1 = protobufModel.groupsMap[PARAM_GROUP1]!!

        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.booleanParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint32ParameterCount, `is`(2))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.floatParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.enumParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.stringParameterCount, `is`(0))


        val param1 = group1.booleanParameterList[0]
        assertThat(param1.value, `is`(GROUP1_BOOL_PARAM_VALUE))
        assertThat(param1.commonAttributes.cacheEnabled, `is`(GROUP1_BOOL_PARAM_CACHE_ENABLED))

        val param2 = group1.uint32ParameterList[0]
        assertThat(param2.value, `is`(GROUP1_UINT32_PARAM1_VALUE))
        assertThat(param2.commonAttributes.cacheEnabled, `is`(GROUP1_UINT32_PARAM1_CACHE_ENABLED))

        val param3 = group1.uint32ParameterList[1]
        assertThat(param3.value, `is`(GROUP1_UINT32_PARAM2_VALUE))
        assertThat(param3.commonAttributes.cacheEnabled, `is`(GROUP1_UINT32_PARAM2_CACHE_ENABLED))


        val group2 = protobufModel.groupsMap[PARAM_GROUP2]!!

        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.booleanParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int16ParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.floatParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.enumParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.stringParameterCount, `is`(1))

        val param4 = group2.int16ParameterList[0]
        assertThat(param4.value, `is`(GROUP2_INT16_PARAM_VALUE))
        assertThat(param4.commonAttributes.cacheEnabled, `is`(GROUP2_INT16_PARAM_CACHE_ENABLED))

        val param5 = group2.floatParameterList[0]
        assertThat(param5.value, `is`(GROUP2_FLOAT_PARAM_VALUE))
        assertThat(param5.commonAttributes.cacheEnabled, `is`(GROUP2_FLOAT_PARAM_CACHE_ENABLED))

        val param6 = group2.stringParameterList[0]
        assertThat(param6.value, `is`(GROUP2_STRING_PARAM_VALUE))
        assertThat(param6.commonAttributes.cacheEnabled, `is`(GROUP2_STRING_PARAM_CACHE_ENABLED))

        val param7 = group2.enumParameterList[0]
        assertThat(param7.value, `is`(GROUP2_ENUM_PARAM_VALUE))
        assertThat(param7.commonAttributes.cacheEnabled, `is`(GROUP2_ENUM_PARAM_CACHE_ENABLED))

    }

    @Test
    fun testWritingEditedContent() {
        val paramsProtobufImpl = ParametersProtobufImpl()
        paramsProtobufImpl.fromBytes(buildTestReading(getFullProtobufModel()))

        val paramGroup1 = paramsProtobufImpl[PARAM_GROUP1]!!.apply { sortBy { it.id } }

        paramGroup1[0].apply {
            cacheEnabled = !cacheEnabled
            setValue(!GROUP1_BOOL_PARAM_VALUE)
        }

        val newUint32Param1Value = 389L
        paramGroup1[1].apply {
            cacheEnabled = !cacheEnabled
            setValue(newUint32Param1Value)
        }

        val newUint32Param2Value = 14389L
        paramGroup1[2].apply {
            cacheEnabled = !cacheEnabled
            setValue(newUint32Param2Value)
        }


        val paramGroup2 = paramsProtobufImpl[PARAM_GROUP2]!!.apply { sortBy { it.id } }

        val newInt16ParamValue = 5473
        paramGroup2[0].apply {
            cacheEnabled = !cacheEnabled
            setValue(newInt16ParamValue)
        }

        val newFloatParamValue = 941.3578f
        paramGroup2[1].apply {
            cacheEnabled = !cacheEnabled
            setValue(newFloatParamValue)
        }

        val newEnumParamValue = 1L
        paramGroup2[2].apply {
            cacheEnabled = !cacheEnabled
            setValue(newEnumParamValue)
        }

        val newStringParamValue = "a new string"
        paramGroup2[3].apply {
            cacheEnabled = !cacheEnabled
            setValue(newStringParamValue)
        }



        val bytes = paramsProtobufImpl.toBytes()
        val protobufModel = ParametersProtobufModel.Parameters.parseFrom(
            bytes.sliceArray(IntRange(0,bytes.size - 5))
        )

        assertThat(protobufModel.groupsCount, `is`(2)) //2 param group

        val group1 = protobufModel.groupsMap[PARAM_GROUP1]!!

        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.booleanParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint32ParameterCount, `is`(2))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.uint8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.int8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.floatParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.enumParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP1]?.stringParameterCount, `is`(0))


        val param1 = group1.booleanParameterList[0]
        assertThat(param1.value, `is`(!GROUP1_BOOL_PARAM_VALUE))
        assertThat(param1.commonAttributes.cacheEnabled, `is`(!GROUP1_BOOL_PARAM_CACHE_ENABLED))

        val param2 = group1.uint32ParameterList[0]
        assertThat(param2.value.toLong(), `is`(newUint32Param1Value))
        assertThat(param2.commonAttributes.cacheEnabled, `is`(!GROUP1_UINT32_PARAM1_CACHE_ENABLED))

        val param3 = group1.uint32ParameterList[1]
        assertThat(param3.value.toLong(), `is`(newUint32Param2Value))
        assertThat(param3.commonAttributes.cacheEnabled, `is`(!GROUP1_UINT32_PARAM2_CACHE_ENABLED))


        val group2 = protobufModel.groupsMap[PARAM_GROUP2]!!

        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.booleanParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint16ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.uint8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int32ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int16ParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.int8ParameterCount, `is`(0))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.floatParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.enumParameterCount, `is`(1))
        assertThat(protobufModel.groupsMap[PARAM_GROUP2]?.stringParameterCount, `is`(1))

        val param4 = group2.int16ParameterList[0]
        assertThat(param4.value, `is`(newInt16ParamValue))
        assertThat(param4.commonAttributes.cacheEnabled, `is`(!GROUP2_INT16_PARAM_CACHE_ENABLED))

        val param5 = group2.floatParameterList[0]
        assertThat(param5.value, `is`(newFloatParamValue))
        assertThat(param5.commonAttributes.cacheEnabled, `is`(!GROUP2_FLOAT_PARAM_CACHE_ENABLED))

        val param6 = group2.stringParameterList[0]
        assertThat(param6.value, `is`(newStringParamValue))
        assertThat(param6.commonAttributes.cacheEnabled, `is`(!GROUP2_STRING_PARAM_CACHE_ENABLED))

        val param7 = group2.enumParameterList[0]
        assertThat(param7.value.toLong(), `is`(newEnumParamValue))
        assertThat(param7.commonAttributes.cacheEnabled, `is`(!GROUP2_ENUM_PARAM_CACHE_ENABLED))

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