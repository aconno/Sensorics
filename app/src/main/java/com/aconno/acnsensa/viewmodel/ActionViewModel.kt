package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.IntentProviderImpl
import com.aconno.acnsensa.device.notification.NotificationDisplayImpl
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.ifttt.GeneralAction
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.ifttt.NotificationOutcome

/**
 * @author aconno
 */
class ActionViewModel(private val addActionUseCase: AddActionUseCase, application: Application) :
    AndroidViewModel(application) {

    val addActionResults: MutableLiveData<Boolean> = MutableLiveData()

    fun addAction(
        name: String,
        sensorType: Int,
        conditionType: String,
        value: String,
        outcomeMessage: String
    ) {
        try {
            val type = when (conditionType) {
                ">" -> 1
                "<" -> 0
                else -> throw IllegalArgumentException("Got invalid sensor type: $conditionType")
            }
            val condition = LimitCondition(sensorType, value.toFloat(), type)
            val outcome = NotificationOutcome(
                outcomeMessage, NotificationDisplayImpl(
                    NotificationFactory(), IntentProviderImpl(), getApplication()
                )
            )
            val action = GeneralAction(name, condition, outcome)
            addActionUseCase.execute(action)
                .subscribe({ onAddActionSuccess() }, { onAddActionFail() })
        } catch (e: Exception) {
            onAddActionFail()
        }
    }

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

    fun onAddActionSuccess() {
        addActionResults.value = true
    }

    fun onAddActionFail() {
        addActionResults.value = false
    }

    fun getConditionTypes(): List<String> {
        return listOf(">", "<")
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
