package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aconno.acnsensa.R

class ActionOptionsViewModel(
    application: Application
) : AndroidViewModel(application) {
    fun getSensorTypes(): List<String> {
        val application: Application = getApplication()

        return listOf(
            application.getString(R.string.temperature),
            application.getString(R.string.light),
            application.getString(R.string.humidity),
            application.getString(R.string.pressure),
            application.getString(R.string.magnetometer_x),
            application.getString(R.string.magnetometer_y),
            application.getString(R.string.magnetometer_z),
            application.getString(R.string.accelerometer_x),
            application.getString(R.string.accelerometer_y),
            application.getString(R.string.accelerometer_z),
            application.getString(R.string.gyro_x),
            application.getString(R.string.gyro_y),
            application.getString(R.string.gyro_z),
            application.getString(R.string.battery_level)
        )
    }

    fun getConditionTypes(): List<String> {
        return listOf(">", "<")
    }

    fun getOuputTypes(): List<String> {
        val application: Application = getApplication()

        return listOf(
            application.getString(R.string.phone_notification),
            application.getString(R.string.sms_message),
            application.getString(R.string.vibration),
            application.getString(R.string.text_to_speech)
        )
    }
}