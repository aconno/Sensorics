package de.troido.acnsensa.data

enum class Axis { X, Y, Z }

/**
 * Used as a tag of sorts for sensor records which utilize axis information, such as
 * [MagnetometerAxis] and [AccelerometerAxis].
 */
interface AxisComponent {
    val axis: Axis
}
