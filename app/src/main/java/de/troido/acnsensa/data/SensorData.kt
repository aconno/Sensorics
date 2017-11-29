package de.troido.acnsensa.data

sealed class SensorData

sealed class SensorRaw : SensorData() {
    abstract fun asList(): List<Sensor<*>>
}

data class SensorRaw1(val gyroscope: AxisVector<GyroscopeAxis>,
                      val accelerometer: AxisVector<AccelerometerAxis>,
                      val magnetometer: AxisVector<MagnetometerAxis>) : SensorRaw() {

    override fun asList(): List<Sensor<*>> =
            listOf(gyroscope.x, gyroscope.y, gyroscope.z,
                    accelerometer.x, accelerometer.y, accelerometer.z,
                    magnetometer.x, magnetometer.y, magnetometer.z)
}

data class SensorRaw2(val temperature: Temperature,
                      val humidity: Humidity,
                      val pressure: Pressure,
                      val light: Light) : SensorRaw() {

    override fun asList(): List<Sensor<*>> =
            listOf(temperature, humidity, pressure, light)
}
