package com.aconno.sensorics.data

import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.ifttt.GeneralRestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test

class DataStringConverterTest {

    private lateinit var list: List<Reading>
    private lateinit var nameValueList: List<RestHttpGetParam>
    private lateinit var temperatureLightList: List<RestHttpGetParam>
    private lateinit var temperatureNameValueList: List<RestHttpGetParam>
    private lateinit var withoutNameTemperatureList: List<RestHttpGetParam>

    @Before
    fun setUp() {
        val device = Device(
            "Device",
            "Alias",
            "MA:CA:DD:RE:SS"
        )

        val reading1 = Reading(System.currentTimeMillis(), device, 30, "Temperature",0, "CF01")
        val reading2 = Reading(System.currentTimeMillis(), device, 40, "Light", 0,"CF01")
        val reading3 = Reading(System.currentTimeMillis(), device, 50, "Accelerometer X", 0,"CF01")

        list = listOf(reading1, reading2, reading3)

        val param1 = GeneralRestHttpGetParam(1, 1, "\$name", "\$value")
        val param2 = GeneralRestHttpGetParam(1, 1, "\$name", "\$value")

        nameValueList = listOf(param1, param2)

        val param3 = GeneralRestHttpGetParam(1, 1, "Temperature", "\$temperature")
        val param4 = GeneralRestHttpGetParam(1, 1, "Light", "\$light")

        temperatureLightList = listOf(param3, param4)


        val param5 = GeneralRestHttpGetParam(1, 1, "Name", "\$name")
        val param6 = GeneralRestHttpGetParam(1, 1, "Temperature", "\$temperature")

        temperatureNameValueList = listOf(param5, param6)

        val param7 = GeneralRestHttpGetParam(1, 1, "Name", "Blabla")
        val param8 = GeneralRestHttpGetParam(1, 1, "Temperature", "Blabla")

        withoutNameTemperatureList = listOf(param7, param8)
    }

    @Test
    fun newDataStringConverterTest() {
        println(" -----  1st Test Starts -----")
        ////
        val dataString1 = "{" +
                "\"temperature\":\"\$temperature\"," +
                "\"light\":\"\$light\"," +
                "\"accelerometer x\":\"\$accelerometer_x\"" +
                "}"

        val newDataStringConverter1 = DataStringConverter()
        println(newDataStringConverter1.convert(list, dataString1))

        println(" -----  2nd Test Starts -----")
        ////
        val dataString2 = "{" +
                "\"name\":\"\$name\"," +
                "\"light\":\"\$light\"," +
                "\"value\":\"\$value\"," +
                "\"accelerometer x\":\"\$accelerometer_x\"" +
                "}"

        val newDataStringConverter2 = DataStringConverter()
        println(newDataStringConverter2.convert(list, dataString2))

        println(" -----  3rd Test Starts -----")
        ///
        val dataString3 = "{" +
                "\"name\":\"\$name\"," +
                "\"value\":\"\$value\"" +
                "}"

        val newDataStringConverter3 = DataStringConverter()
        println(newDataStringConverter3.convert(list, dataString3))


        println(" -----  4th Test Starts -----")
        ///
        val dataString4 = "{" +
                "\"name\":\"name\"," +
                "\"value\":\"value\"" +
                "}"

        val newDataStringConverter4 = DataStringConverter()
        println(newDataStringConverter4.convert(list, dataString4))
    }

    @Test
    fun httpGetParameterTest() {
        println(" -----  1st Test Starts -----")
        ////
        val json1 = Gson().toJson(nameValueList)

        val newDataStringConverter1 = DataStringConverter()
        val message1 = newDataStringConverter1.convert(list, json1)
        println(message1)

        val httpGetType = object : TypeToken<List<GeneralRestHttpGetParam>>() {}.type
        val httpGetParamList1 =
            Gson().fromJson<List<GeneralRestHttpGetParam>>(message1[0], httpGetType)
        println(httpGetParamList1[0].key + " " + httpGetParamList1[0].value)

        println(" -----  2nd Test Starts -----")
        ////
        val json2 = Gson().toJson(temperatureLightList)

        val newDataStringConverter2 = DataStringConverter()
        println(newDataStringConverter2.convert(list, json2))

        println(" -----  3rd Test Starts -----")
        ////
        val json3 = Gson().toJson(temperatureNameValueList)

        val newDataStringConverter3 = DataStringConverter()
        println(newDataStringConverter3.convert(list, json3))


        println(" -----  4th Test Starts -----")
        ////
        val json4 = Gson().toJson(withoutNameTemperatureList)

        val newDataStringConverter4 = DataStringConverter()
        println(newDataStringConverter4.convert(list, json4))
    }
}
