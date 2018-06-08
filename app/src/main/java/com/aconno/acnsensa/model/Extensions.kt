package com.aconno.acnsensa.model

import android.content.Context
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.model.SensorTypeSingle

fun SensorTypeSingle.toStringResource(context: Context): String {
    return when (this) {
        SensorTypeSingle.TEMPERATURE -> context.getString(R.string.temperature)
        SensorTypeSingle.LIGHT -> context.getString(R.string.light)
        SensorTypeSingle.HUMIDITY -> context.getString(R.string.humidity)
        SensorTypeSingle.PRESSURE -> context.getString(R.string.pressure)
        SensorTypeSingle.MAGNETOMETER_X -> context.getString(R.string.magnetometer_x)
        SensorTypeSingle.MAGNETOMETER_Y -> context.getString(R.string.magnetometer_y)
        SensorTypeSingle.MAGNETOMETER_Z -> context.getString(R.string.magnetometer_z)
        SensorTypeSingle.ACCELEROMETER_X -> context.getString(R.string.accelerometer_x)
        SensorTypeSingle.ACCELEROMETER_Y -> context.getString(R.string.accelerometer_y)
        SensorTypeSingle.ACCELEROMETER_Z -> context.getString(R.string.accelerometer_z)
        SensorTypeSingle.GYROSCOPE_X -> context.getString(R.string.gyro_x)
        SensorTypeSingle.GYROSCOPE_Y -> context.getString(R.string.gyro_y)
        SensorTypeSingle.GYROSCOPE_Z -> context.getString(R.string.gyro_z)
        SensorTypeSingle.BATTERY_LEVEL -> context.getString(R.string.battery_level)
    }
}

fun SensorTypeSingle.toInt(): Int {
    return when (this) {
        SensorTypeSingle.TEMPERATURE -> 0
        SensorTypeSingle.LIGHT -> 1
        SensorTypeSingle.HUMIDITY -> 2
        SensorTypeSingle.PRESSURE -> 3
        SensorTypeSingle.MAGNETOMETER_X -> 4
        SensorTypeSingle.MAGNETOMETER_Y -> 5
        SensorTypeSingle.MAGNETOMETER_Z -> 6
        SensorTypeSingle.ACCELEROMETER_X -> 7
        SensorTypeSingle.ACCELEROMETER_Y -> 8
        SensorTypeSingle.ACCELEROMETER_Z -> 9
        SensorTypeSingle.GYROSCOPE_X -> 10
        SensorTypeSingle.GYROSCOPE_Y -> 11
        SensorTypeSingle.GYROSCOPE_Z -> 12
        SensorTypeSingle.BATTERY_LEVEL -> 13
    }
}

fun Int.toSensorType(): SensorTypeSingle {
    return when (this) {
        0 -> SensorTypeSingle.TEMPERATURE
        1 -> SensorTypeSingle.LIGHT
        2 -> SensorTypeSingle.HUMIDITY
        3 -> SensorTypeSingle.PRESSURE
        4 -> SensorTypeSingle.MAGNETOMETER_X
        5 -> SensorTypeSingle.MAGNETOMETER_Y
        6 -> SensorTypeSingle.MAGNETOMETER_Z
        7 -> SensorTypeSingle.ACCELEROMETER_X
        8 -> SensorTypeSingle.ACCELEROMETER_Y
        9 -> SensorTypeSingle.ACCELEROMETER_Z
        10 -> SensorTypeSingle.GYROSCOPE_X
        11 -> SensorTypeSingle.GYROSCOPE_Y
        12 -> SensorTypeSingle.GYROSCOPE_Z
        13 -> SensorTypeSingle.BATTERY_LEVEL
        else -> throw IllegalArgumentException("Int value is not valid SensorType identifier")
    }
}

fun Condition.toString(context: Context): String {
    val sensor = sensorType.toStringResource(context)
    //TODO: Refactor constraint type
    val constraint = when (type) {
        LimitCondition.MORE_THAN -> ">"
        LimitCondition.LESS_THAN -> "<"
        else -> throw IllegalArgumentException("Int is not valid constraint identifier: $type")
    }
    val value = limit.toString()
    return "$sensor $constraint $value"
}