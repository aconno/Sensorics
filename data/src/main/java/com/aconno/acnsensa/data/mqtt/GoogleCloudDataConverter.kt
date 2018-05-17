package com.aconno.acnsensa.data.mqtt

import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import java.text.SimpleDateFormat
import java.util.*

object GoogleCloudDataConverter {

    val date = Date()
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    init {
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convert(reading: Reading): List<String> {
        return when (reading.sensorType) {
            SensorType.TEMPERATURE -> convertTemperature(reading)
            SensorType.LIGHT -> convertLight(reading)
            SensorType.HUMIDITY -> convertHumidity(reading)
            SensorType.PRESSURE -> convertPressure(reading)
            SensorType.MAGNETOMETER -> convertMagnetometer(reading)
            SensorType.ACCELEROMETER -> convertAccelerometer(reading)
            SensorType.GYROSCOPE -> generateGyroscopeMessages(reading)
            SensorType.BATTERY_LEVEL -> generateBatteryMessage(reading)

            else -> throw IllegalArgumentException("Got invalid reading type.")
        }
    }

    private fun convertTemperature(reading: Reading): List<String> {
        return listOf(getJsonString("Temperature", reading.timestamp, reading.values[0]))
    }

    private fun convertLight(reading: Reading): List<String> {
        return listOf(getJsonString("Light", reading.timestamp, reading.values[0]))
    }

    private fun convertHumidity(reading: Reading): List<String> {
        return listOf(getJsonString("Humidity", reading.timestamp, reading.values[0]))
    }

    private fun convertPressure(reading: Reading): List<String> {
        return listOf(getJsonString("Pressure", reading.timestamp, reading.values[0]))
    }

    private fun convertMagnetometer(reading: Reading): List<String> {
        return listOf(
            getJsonString("Magnetometer X", reading.timestamp, reading.values[0]),
            getJsonString("Magnetometer Y", reading.timestamp, reading.values[1]),
            getJsonString("Magnetometer Z", reading.timestamp, reading.values[2])
        )
    }

    private fun convertAccelerometer(reading: Reading): List<String> {
        return listOf(
            getJsonString("Accelerometer X", reading.timestamp, reading.values[0]),
            getJsonString("Accelerometer Y", reading.timestamp, reading.values[1]),
            getJsonString("Accelerometer Z", reading.timestamp, reading.values[2])
        )
    }

    private fun generateGyroscopeMessages(reading: Reading): List<String> {
        return listOf(
            getJsonString("Gyroscope X", reading.timestamp, reading.values[0]),
            getJsonString("Gyroscope Y", reading.timestamp, reading.values[1]),
            getJsonString("Gyroscope Z", reading.timestamp, reading.values[2])
        )
    }

    private fun generateBatteryMessage(reading: Reading): List<String> {
        return listOf(getJsonString("Battery level", reading.timestamp, reading.values[0]))
    }

    private fun getJsonString(sensorType: String, timestamp: Long, value: Number): String {
        date.time = timestamp
        return "{\n" +
                "  \"type\": \"$sensorType\",\n" +
                "  \"timestamp\": \"${timeFormat.format(date)}\",\n" +
                "  \"value\": $value\n" +
                "}"
    }
}