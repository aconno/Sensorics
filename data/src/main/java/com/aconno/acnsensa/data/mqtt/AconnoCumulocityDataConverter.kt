package com.aconno.acnsensa.data.mqtt

import com.aconno.acnsensa.domain.model.readings.*

object AconnoCumulocityDataConverter {

    fun convert(reading: Reading): List<String> {
        return when (reading) {
            is TemperatureReading -> convertTemperature(reading)
            is LightReading -> convertLight(reading)
            is HumidityReading -> convertHumidity(reading)
            is PressureReading -> convertPressure(reading)
            is MagnetometerReading -> convertMagnetometer(reading)
            is AccelerometerReading -> convertAccelerometer(reading)
            is GyroscopeReading -> generateGyroscopeMessages(reading)
            is BatteryReading -> generateBatteryMesssage(reading)

            else -> throw IllegalArgumentException("Got invalid reading type.")
        }
    }

    private fun convertTemperature(reading: TemperatureReading): List<String> {
        return listOf("200,Temperature,Result,${reading.temperature},Celcius,${reading.timestamp}")
    }

    private fun convertLight(reading: LightReading): List<String> {
        return listOf("200,Light,Result,${reading.light},%,${reading.timestamp}")
    }

    private fun convertHumidity(reading: HumidityReading): List<String> {
        return listOf("200,Humidity,Result,${reading.humidity},%,${reading.timestamp}")
    }

    private fun convertPressure(reading: PressureReading): List<String> {
        return listOf("200,Pressure,Result,${reading.pressure},hPa,${reading.timestamp}")
    }

    private fun convertMagnetometer(reading: MagnetometerReading): List<String> {
        return listOf(
            "200,Magnetometer X,Result,${reading.magnetometerX},uT,${reading.timestamp}",
            "200,Magnetometer Y,Result,${reading.magnetometerY},uT,${reading.timestamp}",
            "200,Magnetometer Z,Result,${reading.magnetometerZ},uT,${reading.timestamp}"
        )
    }

    private fun convertAccelerometer(reading: AccelerometerReading): List<String> {
        return listOf(
            "200,Accelerometer X,Result,${reading.accelerometerX},uT,${reading.timestamp}",
            "200,Accelerometer Y,Result,${reading.accelerometerY},uT,${reading.timestamp}",
            "200,Accelerometer Z,Result,${reading.accelerometerZ},uT,${reading.timestamp}"
        )
    }

    private fun generateGyroscopeMessages(reading: GyroscopeReading): List<String> {
        return listOf(
            "200,Gyroscope X,Result,${reading.gyroscopeX},uT,${reading.timestamp}",
            "200,Gyroscope Y,Result,${reading.gyroscopeY},uT,${reading.timestamp}",
            "200,Gyroscope Z,Result,${reading.gyroscopeZ},uT,${reading.timestamp}"
        )
    }

    private fun generateBatteryMesssage(reading: BatteryReading): List<String> {
        return listOf("200,Battery Level,${reading.batteryLevel},%,${reading.timestamp}")
    }
}