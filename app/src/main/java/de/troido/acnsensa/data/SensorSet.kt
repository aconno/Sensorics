package de.troido.acnsensa.data

/**
 * A set of individual sensor's data.
 */
data class SensorSet(val light: Light,
                     val temperature: Temperature,
                     val accelerometerX: AccelerometerAxis,
                     val accelerometerY: AccelerometerAxis,
                     val accelerometerZ: AccelerometerAxis,
                     val magnetometerX: MagnetometerAxis,
                     val magnetometerY: MagnetometerAxis,
                     val magnetometerZ: MagnetometerAxis) {

    fun asList(): List<Sensor<*>> =
            listOf(light, temperature,
                    accelerometerX, accelerometerY, accelerometerZ,
                    magnetometerX, magnetometerY, magnetometerZ)
}
