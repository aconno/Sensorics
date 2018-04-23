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

class ExistingActionViewModel(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val notificationDisplay: NotificationDisplay,
    private val vibrator: Vibrator,
    private val smsSender: SmsSender,
    private val textToSpeechPlayer: TextToSpeechPlayer,
    application: Application
) : AndroidViewModel(application) {

    val action: MutableLiveData<Action> = MutableLiveData()

    fun getActionById(actionId: Long) {
        getActionByIdUseCase.execute(actionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onActionFound(it) },
                { Timber.e("Failed to get action") })
    }

    private fun onActionFound(action: Action) {
        this.action.value = action
    }

    fun updateAction(
        name: String,
        sensorType: Int,
        conditionType: String,
        value: String,
        outcomeType: String,
        smsDestination: String,
        content: String
    ) {
        var updatedAction: Action? = null

        try {
            val type = when (conditionType) {
                ">" -> 1
                "<" -> 0
                else -> throw IllegalArgumentException("Got invalid sensor type: $conditionType")
            }
            val condition = LimitCondition(sensorType, value.toFloat(), type)


            val loadedId = action.value?.id ?: 0

            when (outcomeType) {
                getApplication<Application>().getString(R.string.phone_notification) -> {
                    val outcome = NotificationOutcome(content, notificationDisplay)
                    updatedAction = GeneralAction(loadedId, name, condition, outcome)
                }
                getApplication<Application>().getString(R.string.sms_message) -> {
                    val outcome = SmsOutcome(smsSender, smsDestination, content)
                    updatedAction = GeneralAction(loadedId, name, condition, outcome)
                }
                getApplication<Application>().getString(R.string.vibration) -> {
                    val outcome = VibrationOutcome(vibrator)
                    updatedAction = GeneralAction(loadedId, name, condition, outcome)
                }
                getApplication<Application>().getString(R.string.text_to_speech)->{
                    val outcome = TextToSpeechOutcome(content, textToSpeechPlayer)
                    updatedAction = GeneralAction(loadedId, name, condition, outcome)
                }
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
        updatedAction?.let {
            updateActionUseCase.execute(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.e("Updated action successfully") },
                    { Timber.e("Failed to update action") })
        }
    }

    fun deleteAction() {
        action.value?.let {
            deleteActionUseCase.execute(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.e("Deleted action successfully") },
                    { Timber.e("Failed to delete action") })
        }
    }
}