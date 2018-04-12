package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
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
        outcomeMessage: String,
        vibrate: Boolean,
        smsDestination: String,
        smsMessage: String
    ) {
        try {
            val type = when (conditionType) {
                ">" -> 0
                "<" -> 1
                else -> throw IllegalArgumentException("Got invalid sensor type: $conditionType")
            }
            val condition = LimitCondition(sensorType, value.toFloat(), type)

            val actions = mutableListOf<Action>()
            if (outcomeMessage.isNotEmpty()) {
                val outcome = NotificationOutcome(
                    outcomeMessage, notificationDisplay
                )
                actions.add(GeneralAction(0, name, condition, outcome))
            }

            if (vibrate) {
                val outcome = VibrationOutcome(vibrator)
                actions.add(GeneralAction(0, name, condition, outcome))
            }

            if (smsDestination.isNotEmpty() && smsMessage.isNotEmpty()) {
                val outcome = SmsOutcome(smsSender, smsDestination, smsMessage)
                actions.add(GeneralAction(0, name, condition, outcome))
            }

            actions.forEach {
                addActionUseCase.execute(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ onAddActionSuccess() }, { onAddActionFail() })
            }

        } catch (e: Exception) {
            Timber.e(e)
            onAddActionFail()
        }
    }


    fun onAddActionSuccess() {
        addActionResults.value = true
    }

    fun onAddActionFail() {
        addActionResults.value = false
    }
}
