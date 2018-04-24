package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.GeneralAction
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.domain.interactor.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateActionUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ExistingActionViewModel(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
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

            val parameters = mapOf(
                Pair(Outcome.TEXT_MESSAGE, content), Pair(Outcome.PHONE_NUMBER, smsDestination)
            )

            val outcomeEndType = when (outcomeType) {
                getApplication<Application>().getString(R.string.phone_notification) -> {
                    Outcome.OUTCOME_TYPE_NOTIFICATION
                }
                getApplication<Application>().getString(R.string.sms_message) -> {
                    Outcome.OUTCOME_TYPE_SMS
                }
                getApplication<Application>().getString(R.string.vibration) -> {
                    Outcome.OUTCOME_TYPE_VIBRATION
                }
                getApplication<Application>().getString(R.string.text_to_speech) -> {
                    Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH
                }
                else -> throw IllegalArgumentException("Invalid outcome type")
            }

            val outcome = Outcome(parameters, outcomeEndType)
            updatedAction = GeneralAction(loadedId, name, condition, outcome)

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