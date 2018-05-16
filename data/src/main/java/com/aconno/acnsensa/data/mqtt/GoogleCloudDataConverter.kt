package com.aconno.acnsensa.data.mqtt

import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.*

object GoogleCloudDataConverter {

    fun convert(reading: Reading): List<String> {
        return when (reading.sensorType) {
            SensorType.TEMPERATURE -> convertTemperature(reading)
            SensorType.LIGHT -> convertLight(reading)
            SensorType.HUMIDITY -> convertHumidity(reading)
            SensorType.PRESSURE -> convertPressure(reading)
            SensorType.MAGNETOMETER -> convertMagnetometer(reading)
            SensorType.ACCELEROMETER -> convertAccelerometer(reading)
            SensorType.GYROSCOPE -> generateGyroscopeMessages(reading)
            SensorType.BATTERY_LEVEL -> generateBatteryMesssage(reading)

            else -> throw IllegalArgumentException("Got invalid reading type.")
        }
    }

    private fun convertTemperature(reading: Reading): List<String> {
        return listOf("Temperature,${reading.values[0]},${reading.timestamp}")
    }

    private fun convertLight(reading: Reading): List<String> {
        return listOf("Light,${reading.values[0]},${reading.timestamp}")
    }

    private fun convertHumidity(reading: Reading): List<String> {
        return listOf("Humidity,${reading.values[0]},${reading.timestamp}")
    }

    private fun convertPressure(reading: Reading): List<String> {
        return listOf("Pressure,${reading.values[0]},${reading.timestamp}")
    }

    private fun convertMagnetometer(reading: Reading): List<String> {
        return listOf(
            "Magnetometer X,${reading.values[0]},${reading.timestamp}",
            "Magnetometer Y,${reading.values[1]},${reading.timestamp}",
            "Magnetometer Z,${reading.values[2]},${reading.timestamp}"
        )
    }

    private fun convertAccelerometer(reading: Reading): List<String> {
        return listOf(
            "Accelerometer X,${reading.values[0]},${reading.timestamp}",
            "Accelerometer Y,${reading.values[1]},${reading.timestamp}",
            "Accelerometer Z,${reading.values[2]},${reading.timestamp}"
        )
    }

    private fun generateGyroscopeMessages(reading: Reading): List<String> {
        return listOf(
            "Gyroscope X,${reading.values[0]},${reading.timestamp}",
            "Gyroscope Y,${reading.values[1]},${reading.timestamp}",
            "Gyroscope Z,${reading.values[2]},${reading.timestamp}"
        )
    }

    private fun generateBatteryMesssage(reading: Reading): List<String> {
        return listOf("Battery Level,${reading.values[0]},${reading.timestamp}")
    }
}