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
        SensorTypeSingle.OTHER -> "Other"
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