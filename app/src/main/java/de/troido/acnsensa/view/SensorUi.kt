package de.troido.acnsensa.view

import android.support.annotation.DrawableRes
import de.troido.acnsensa.R
import de.troido.acnsensa.data.*

fun uiTitle(axis: Axis): String = when (axis) {
    Axis.X -> "x"
    Axis.Y -> "y"
    Axis.Z -> "z"
}

fun uiTitle(sensor: Sensor<*>): String = when (sensor) {
    is AccelerometerAxis -> "Accelerometer ${uiTitle(sensor.axis)}"
    is MagnetometerAxis -> "Magnetometer ${uiTitle(sensor.axis)}"
    is GyroscopeAxis -> "Gyroscope ${uiTitle(sensor.axis)}"
    is Temperature -> "Temperature"
    is Light -> "Light"
    is Humidity -> "Humidity"
    is Pressure -> "Pressure"
}

fun uiText(sensor: Sensor<*>): String = when (sensor) {
    is AccelerometerAxis -> "%.2f mg"
    is MagnetometerAxis -> "%.2f μT"
    is Temperature -> "%.2f °C"
    is Light -> "%.2f %%"
    is GyroscopeAxis -> "%.2f dps"
    is Humidity -> "%.2f %%"
    is Pressure -> "%.2f hPa"
}.format(sensor.value)

@DrawableRes
fun uiImage(sensor: Sensor<*>): Int = when (sensor) {
    is AccelerometerAxis -> R.drawable.ic_acc
    is MagnetometerAxis -> R.drawable.ic_compass
    is Temperature -> R.drawable.ic_temperature
    is Light -> R.drawable.ic_light
    is GyroscopeAxis -> R.drawable.ic_gyro
    is Humidity -> R.drawable.ic_humidity
    is Pressure -> R.drawable.ic_pressure
}
