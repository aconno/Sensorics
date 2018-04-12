package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel

class ActionOptionsViewModel : ViewModel() {
    fun getSensorTypes(): List<String> {
        return listOf(
            TEMPERATURE,
            LIGHT,
            HUMIDITY,
            PRESSURE,
            MAGNETOMETER_X,
            MAGNETOMETER_Y,
            MAGNETOMETER_Z,
            ACCELEROMETER_X,
            ACCELEROMETER_Y,
            ACCELEROMETER_Z,
            GYROSCOPE_X,
            GYROSCOPE_Y,
            GYROSCOPE_Z,
            BATTERY_LEVEL
        )
    }

    fun getConditionTypes(): List<String> {
        return listOf(">", "<")
    }

    fun getOuputTypes(): List<String> {
        return listOf(
            "Phone Notification",
            "SMS Message",
            "Vibration"
        )
    }

    companion object {
        const val TEMPERATURE = "Temperature"
        const val LIGHT = "Light"
        const val HUMIDITY = "Humidity"
        const val PRESSURE = "Pressure"
        const val MAGNETOMETER_X = "Magnetometer X"
        const val MAGNETOMETER_Y = "Magnetometer Y"
        const val MAGNETOMETER_Z = "Magnetometer Z"
        const val ACCELEROMETER_X = "Accelerometer X"
        const val ACCELEROMETER_Y = "Accelerometer Y"
        const val ACCELEROMETER_Z = "Accelerometer Z"
        const val GYROSCOPE_X = "Gyroscope X"
        const val GYROSCOPE_Y = "Gyroscope Y"
        const val GYROSCOPE_Z = "Gyroscope Z"
        const val BATTERY_LEVEL = "Battery Level"
    }
}