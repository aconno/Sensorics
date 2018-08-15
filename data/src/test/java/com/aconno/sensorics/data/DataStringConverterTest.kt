package com.aconno.sensorics.data

import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DataStringConverterTest {

    @Test
    fun testDataString() {
        val str = "{'asdf':'\$temperature_x'\$name-\$value}this is a\$light test thing{'\$name':'asd'}"
        val dataStringConverter = DataStringConverter(str)

        val reading1 = Reading(
            System.currentTimeMillis(), Device(
                "name", "alias", "MA:CA:DD:RE:SS"
            ), 35, "Temperature X"
        )

        val reading2 = Reading(
            System.currentTimeMillis(), Device(
                "name", "alias", "MA:CA:DD:RE:SS"
            ), 35, "Light"
        )

        val reading3 = Reading(
            System.currentTimeMillis(), Device(
                "name", "alias", "MA:CA:DD:RE:SS"
            ), 35, "Gyro"
        )

        println(dataStringConverter.convert(reading1))
        println(dataStringConverter.convert(reading2))
        println(dataStringConverter.convert(reading3))
    }
}