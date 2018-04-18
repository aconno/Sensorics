package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * @author aconno
 */
class NewActionViewModel(
    private val addActionUseCase: AddActionUseCase,
    private val notificationDisplay: NotificationDisplay,
    private val vibrator: Vibrator,
    private val smsSender: SmsSender,
    application: Application
) :
    AndroidViewModel(application) {

    val addActionResults: MutableLiveData<Boolean> = MutableLiveData()

    fun addAction(
        name: String,
        sensorType: Int,
        conditionType: String,
        value: String,
        outcomeType: String,
        smsDestination: String,
        content: String
    ) {
        var newAction: Action? = null
        try {
            val type = when (conditionType) {
                ">" -> 1
                "<" -> 0
                else -> throw IllegalArgumentException("Got invalid sensor type: $conditionType")
            }
            val condition = LimitCondition(sensorType, value.toFloat(), type)


            val newId = 0L

            when (outcomeType) {
                getApplication<Application>().getString(R.string.phone_notification) -> {
                    val outcome = NotificationOutcome(content, notificationDisplay)
                    newAction = GeneralAction(newId, name, condition, outcome)
                }
                getApplication<Application>().getString(R.string.sms_message) -> {
                    val outcome = SmsOutcome(smsSender, smsDestination, content)
                    newAction = GeneralAction(newId, name, condition, outcome)
                }
                getApplication<Application>().getString(R.string.vibration) -> {
                    val outcome = VibrationOutcome(vibrator)
                    newAction = GeneralAction(newId, name, condition, outcome)
                }
            }

            newAction?.let {
                addActionUseCase.execute(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onAddActionSuccess() }, { onAddActionFail() })
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    fun onAddActionSuccess() {
        addActionResults.value = true
    }

    fun onAddActionFail() {
        addActionResults.value = false
    }
}
