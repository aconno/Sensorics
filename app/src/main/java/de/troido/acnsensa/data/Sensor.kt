package de.troido.acnsensa.data

import de.troido.ekstend.numeric.deadZone

const val ACCELEROMETER_DEAD_ZONE = 0.1f

sealed class Sensor<out V>(val value: V)

/**
 * A single accelerometer axis.
 * A dead zone transformation is applied to values around the zero, so that all values in the range
 * of `-`[ACCELEROMETER_DEAD_ZONE] to [ACCELEROMETER_DEAD_ZONE] go to zero.
 * This eliminates the ever present small fluctuations which are undesirable around a "resting"
 * point such as the zero.
 */
class AccelerometerAxis(value: Float, override val axis: Axis)
    : Sensor<Float>(value.deadZone(ACCELEROMETER_DEAD_ZONE)),
        AxisComponent

class MagnetometerAxis(value: Float, override val axis: Axis)
    : Sensor<Float>(value),
        AxisComponent

class GyroscopeAxis(value: Float, override val axis: Axis)
    : Sensor<Float>(value),
        AxisComponent

class Temperature(value: Float) : Sensor<Float>(value)

class Light(value: Float) : Sensor<Float>(value)

class Humidity(value: Float) : Sensor<Float>(value)

class Pressure(value: Float) : Sensor<Float>(value)
