package com.aconno.sensorics.device.beacon.protobuffers.parameters

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.protobuffers.generatedmodel.ParametersProtobufModel
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class BooleanParamProtobufImplTest {


    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.BooleanParameter.newBuilder()
            .setValue(true)
            .setCommonAttributes(
                ParameterProtobufTestUtils.buildCommonParamAttributes(
                    BOOL_PARAM_WRITABLE, BOOL_PARAM_EVENTABLE,
                    BOOL_PARAM_CACHE_SUPPORTED,
                    BOOL_PARAM_CACHE_ENABLED, BOOL_PARAM_NAME)
            )
            .build()

        val parameter = BooleanParamProtobufImpl(5, ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(BOOL_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(BOOL_PARAM_VALUE))
    }



    companion object {
        const val BOOL_PARAM_WRITABLE = false
        const val BOOL_PARAM_EVENTABLE = false
        const val BOOL_PARAM_CACHE_SUPPORTED = true
        const val BOOL_PARAM_CACHE_ENABLED = true
        const val BOOL_PARAM_NAME = "boolParam1"
        const val BOOL_PARAM_VALUE = true
    }
}

object ParameterProtobufTestUtils {

    val TEST_CONFIG_OBJECT = Parameters.Config(100,200,300)

    fun buildCommonParamAttributes(writable : Boolean,eventable : Boolean,
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
}


class UIntParamProtobufImplTest {

    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.UIntParameter.newBuilder()
            .setValue(UINT_PARAM_VALUE)
            .setCommonAttributes(getCommonParamAttributes())
            .build()

        val parameter = UIntParamProtobufImpl(5, ParameterType.UINT32,ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(UINT_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(UINT_PARAM_VALUE))
    }

    @Test
    //testing loading of value that is too big for int32 but not too big for uint32
    fun testLoadingVeryLargeValue() {
        val protobufModel = ParametersProtobufModel.UIntParameter.newBuilder()
            .setValue(UINT_PARAM_LARGE_VALUE.toInt())
            .setCommonAttributes(getCommonParamAttributes())
            .build()

        val parameter = UIntParamProtobufImpl(5, ParameterType.UINT32,ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        assertThat(parameter.getValue(), `is`(UINT_PARAM_LARGE_VALUE))

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(UINT_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(UINT_PARAM_LARGE_VALUE.toInt()))
    }

    private fun getCommonParamAttributes() : ParametersProtobufModel.CommonParameterAttributes {
        return ParameterProtobufTestUtils.buildCommonParamAttributes(
            UINT_PARAM_WRITABLE, UINT_PARAM_EVENTABLE,
            UINT_PARAM_CACHE_SUPPORTED,
            UINT_PARAM_CACHE_ENABLED, UINT_PARAM_NAME
        )
    }


    companion object {
        const val UINT_PARAM_WRITABLE = false
        const val UINT_PARAM_EVENTABLE = false
        const val UINT_PARAM_CACHE_SUPPORTED = true
        const val UINT_PARAM_CACHE_ENABLED = true
        const val UINT_PARAM_NAME = "uintParam"
        const val UINT_PARAM_VALUE = 23456
        const val UINT_PARAM_LARGE_VALUE = 3154283321L
    }

}



class IntParamProtobufImplTest {

    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.IntParameter.newBuilder()
            .setValue(INT_PARAM_VALUE)
            .setCommonAttributes(getCommonParamAttributes())
            .build()

        val parameter = IntParamProtobufImpl(5, ParameterType.INT32,ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(INT_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(INT_PARAM_VALUE))
    }


    private fun getCommonParamAttributes() : ParametersProtobufModel.CommonParameterAttributes {
        return ParameterProtobufTestUtils.buildCommonParamAttributes(
            INT_PARAM_WRITABLE, INT_PARAM_EVENTABLE,
            INT_PARAM_CACHE_SUPPORTED,
            INT_PARAM_CACHE_ENABLED, INT_PARAM_NAME
        )
    }


    companion object {
        const val INT_PARAM_WRITABLE = false
        const val INT_PARAM_EVENTABLE = false
        const val INT_PARAM_CACHE_SUPPORTED = true
        const val INT_PARAM_CACHE_ENABLED = true
        const val INT_PARAM_NAME = "intParam"
        const val INT_PARAM_VALUE = 23456
    }

}

class FloatParamProtobufImplTest {

    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.FloatParameter.newBuilder()
            .setValue(FLOAT_PARAM_VALUE)
            .setCommonAttributes(getCommonParamAttributes())
            .build()

        val parameter = FloatParamProtobufImpl(5, ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(FLOAT_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(FLOAT_PARAM_VALUE))
    }


    private fun getCommonParamAttributes() : ParametersProtobufModel.CommonParameterAttributes {
        return ParameterProtobufTestUtils.buildCommonParamAttributes(
            FLOAT_PARAM_WRITABLE, FLOAT_PARAM_EVENTABLE,
            FLOAT_PARAM_CACHE_SUPPORTED,
            FLOAT_PARAM_CACHE_ENABLED, FLOAT_PARAM_NAME
        )
    }


    companion object {
        const val FLOAT_PARAM_WRITABLE = false
        const val FLOAT_PARAM_EVENTABLE = false
        const val FLOAT_PARAM_CACHE_SUPPORTED = true
        const val FLOAT_PARAM_CACHE_ENABLED = true
        const val FLOAT_PARAM_NAME = "floatParam"
        const val FLOAT_PARAM_VALUE = 23456.55f
    }

}


class EnumParamProtobufImplTest {

    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.EnumParameter.newBuilder()
            .setValue(ENUM_PARAM_VALUE)
            .setCommonAttributes(getCommonParamAttributes())
            .setUnit(ENUM_PARAM_UNIT)
            .addChoice(ENUM_PARAM_CHOICE1)
            .addChoice(ENUM_PARAM_CHOICE2)
            .addChoice(ENUM_PARAM_CHOICE3)
            .build()

        val parameter = EnumParamProtobufImpl(5, ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(ENUM_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(ENUM_PARAM_VALUE))
    }


    private fun getCommonParamAttributes() : ParametersProtobufModel.CommonParameterAttributes {
        return ParameterProtobufTestUtils.buildCommonParamAttributes(
            ENUM_PARAM_WRITABLE, ENUM_PARAM_EVENTABLE,
            ENUM_PARAM_CACHE_SUPPORTED,
            ENUM_PARAM_CACHE_ENABLED, ENUM_PARAM_NAME
        )
    }


    companion object {
        const val ENUM_PARAM_WRITABLE = true
        const val ENUM_PARAM_EVENTABLE = true
        const val ENUM_PARAM_CACHE_SUPPORTED = false
        const val ENUM_PARAM_CACHE_ENABLED = false
        const val ENUM_PARAM_NAME = "enumParam1"
        const val ENUM_PARAM_VALUE = 2
        const val ENUM_PARAM_UNIT = "enumUnit"
        const val ENUM_PARAM_CHOICE1 = "enumChoice1"
        const val ENUM_PARAM_CHOICE2 = "enumChoice2"
        const val ENUM_PARAM_CHOICE3 = "enumChoice3"
    }

}

class StringParamProtobufImplTest {

    @Test
    fun testExportToProtobufModel() {
        val protobufModel = ParametersProtobufModel.StringParameter.newBuilder()
            .setValue(STRING_PARAM_VALUE)
            .setCommonAttributes(getCommonParamAttributes())
            .build()

        val parameter = StringParamProtobufImpl(22,ParameterProtobufTestUtils.TEST_CONFIG_OBJECT,protobufModel)

        val exportedProtobufModel = parameter.toProtobufModel()

        assertThat(exportedProtobufModel.commonAttributes.cacheEnabled, `is`(STRING_PARAM_CACHE_ENABLED))
        assertThat(exportedProtobufModel.value, `is`(STRING_PARAM_VALUE))
    }


    private fun getCommonParamAttributes() : ParametersProtobufModel.CommonParameterAttributes {
        return ParameterProtobufTestUtils.buildCommonParamAttributes(
            STRING_PARAM_WRITABLE, STRING_PARAM_EVENTABLE,
            STRING_PARAM_CACHE_SUPPORTED,
            STRING_PARAM_CACHE_ENABLED, STRING_PARAM_NAME
        )
    }


    companion object {
        const val STRING_PARAM_WRITABLE = true
        const val STRING_PARAM_EVENTABLE = true
        const val STRING_PARAM_CACHE_SUPPORTED = false
        const val STRING_PARAM_CACHE_ENABLED = false
        const val STRING_PARAM_NAME = "stringParam1"
        const val STRING_PARAM_VALUE = "testStringValue"
    }

}