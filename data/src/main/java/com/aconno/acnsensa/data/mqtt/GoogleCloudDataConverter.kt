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
        return listOf("Temperature,Result,${reading.values[0]},Celcius,${reading.timestamp}")
    }

    private fun convertLight(reading: Reading): List<String> {
        return listOf("Light,Result,${reading.values[0]},%,${reading.timestamp}")
    }

    private fun convertHumidity(reading: Reading): List<String> {
        return listOf("Humidity,Result,${reading.values[0]},%,${reading.timestamp}")
    }

    private fun convertPressure(reading: Reading): List<String> {
        return listOf("Pressure,Result,${reading.values[0]},hPa,${reading.timestamp}")
    }

    private fun convertMagnetometer(reading: Reading): List<String> {
        return listOf(
            "Magnetometer X,Result,${reading.values[0]},uT,${reading.timestamp}",
            "Magnetometer Y,Result,${reading.values[1]},uT,${reading.timestamp}",
            "Magnetometer Z,Result,${reading.values[2]},uT,${reading.timestamp}"
        )
    }

    private fun convertAccelerometer(reading: Reading): List<String> {
        return listOf(
            "Accelerometer X,Result,${reading.values[0]},uT,${reading.timestamp}",
            "Accelerometer Y,Result,${reading.values[1]},uT,${reading.timestamp}",
            "Accelerometer Z,Result,${reading.values[2]},uT,${reading.timestamp}"
        )
    }

    private fun generateGyroscopeMessages(reading: Reading): List<String> {
        return listOf(
            "Gyroscope X,Result,${reading.values[0]},uT,${reading.timestamp}",
            "Gyroscope Y,Result,${reading.values[1]},uT,${reading.timestamp}",
            "Gyroscope Z,Result,${reading.values[2]},uT,${reading.timestamp}"
        )
    }

    private fun generateBatteryMesssage(reading: Reading): List<String> {
        return listOf("Battery Level,${reading.values[0]},%,${reading.timestamp}")
    }
}