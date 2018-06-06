package com.aconno.acnsensa.data.converter

import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.domain.model.readings.Reading
import java.text.SimpleDateFormat
import java.util.*

object PublisherDataConverter {

    val date = Date()
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    init {
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convert(reading: SensorReading): List<String> {
        return when (reading.sensorType) {
            SensorTypeSingle.TEMPERATURE -> convertTemperature(reading)
            SensorTypeSingle.LIGHT -> convertLight(reading)
            SensorTypeSingle.HUMIDITY -> convertHumidity(reading)
            SensorTypeSingle.PRESSURE -> convertPressure(reading)
            SensorTypeSingle.MAGNETOMETER_X -> convertMagnetometerX(reading)
            SensorTypeSingle.MAGNETOMETER_Y -> convertMagnetometerY(reading)
            SensorTypeSingle.MAGNETOMETER_Z -> convertMagnetometerZ(reading)
            SensorTypeSingle.ACCELEROMETER_X -> convertAccelerometerX(reading)
            SensorTypeSingle.ACCELEROMETER_Y -> convertAccelerometerY(reading)
            SensorTypeSingle.ACCELEROMETER_Z -> convertAccelerometerZ(reading)
            SensorTypeSingle.GYROSCOPE_X -> convertGyroscopeX(reading)
            SensorTypeSingle.GYROSCOPE_Y -> convertGyroscopeY(reading)
            SensorTypeSingle.GYROSCOPE_Z -> convertGyroscopeZ(reading)
            SensorTypeSingle.BATTERY_LEVEL -> generateBatteryMessage(reading)

            else -> throw IllegalArgumentException("Got invalid reading type.")
        }
    }

    private fun convertTemperature(reading: SensorReading): List<String> {
        return listOf(getJsonString("Temperature", reading.timestamp, reading.value))
    }

    private fun convertLight(reading: SensorReading): List<String> {
        return listOf(getJsonString("Light", reading.timestamp, reading.value))
    }

    private fun convertHumidity(reading: SensorReading): List<String> {
        return listOf(getJsonString("Humidity", reading.timestamp, reading.value))
    }

    private fun convertPressure(reading: SensorReading): List<String> {
        return listOf(getJsonString("Pressure", reading.timestamp, reading.value))
    }

    private fun convertMagnetometerX(reading: SensorReading): List<String> {
        return listOf(getJsonString("Magnetometer X", reading.timestamp, reading.value))
    }

    private fun convertMagnetometerY(reading: SensorReading): List<String> {
        return listOf(getJsonString("Magnetometer Y", reading.timestamp, reading.value))
    }

    private fun convertMagnetometerZ(reading: SensorReading): List<String> {
        return listOf(getJsonString("Magnetometer Z", reading.timestamp, reading.value))
    }

    private fun convertAccelerometerX(reading: SensorReading): List<String> {
        return listOf(getJsonString("Accelerometer X", reading.timestamp, reading.value))
    }

    private fun convertAccelerometerY(reading: SensorReading): List<String> {
        return listOf(getJsonString("Accelerometer Y", reading.timestamp, reading.value))
    }

    private fun convertAccelerometerZ(reading: SensorReading): List<String> {
        return listOf(getJsonString("Accelerometer Z", reading.timestamp, reading.value))
    }

    private fun convertGyroscopeX(reading: SensorReading): List<String> {
        return listOf(getJsonString("Gyroscope X", reading.timestamp, reading.value))
    }

    private fun convertGyroscopeY(reading: SensorReading): List<String> {
        return listOf(getJsonString("Gyroscope Y", reading.timestamp, reading.value))
    }

    private fun convertGyroscopeZ(reading: SensorReading): List<String> {
        return listOf(getJsonString("Gyroscope Z", reading.timestamp, reading.value))
    }

    private fun generateBatteryMessage(reading: SensorReading): List<String> {
        return listOf(getJsonString("Battery level", reading.timestamp, reading.value))
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