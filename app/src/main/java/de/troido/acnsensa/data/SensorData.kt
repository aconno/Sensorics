package de.troido.acnsensa.data

sealed class SensorData

sealed class SensorRaw : SensorData() {
    abstract fun asList(): List<Sensor<*>>
}

data class SensorCsv(val battery: Byte,
                     val time: Long,
                     val sensors: SensorSet) : SensorData()

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

data class SensorCount(val battery: Byte,
                       val time: Int,
                       val light: Int,
                       val temperature: Int,
                       val accelerometerX: Int,
                       val accelerometerY: Int,
                       val accelerometerZ: Int,
                       val magnetometerX: Int,
                       val magnetometerY: Int,
                       val magnetometerZ: Int) : SensorData()

data class SensorStats1(val time: Int,
                        val battery: Byte,
                        val accelerometer: DescriptiveStats<Vector3F>) : SensorData()

data class SensorStats2(val light: DescriptiveStats<Byte>,
                        val temperature: DescriptiveStats<Byte>,
                        val magnetometer: DescriptiveStats<Vector3F>,
                        private val timeCheck: Byte) : SensorData() {

    fun checkStats1(data: SensorStats1): Boolean =
            data.time and 0xff == timeCheck.toInt()
}
