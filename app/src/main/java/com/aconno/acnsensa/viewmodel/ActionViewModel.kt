package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.Condition
import com.aconno.acnsensa.domain.ifttt.GeneralAction
import com.aconno.acnsensa.domain.ifttt.LimitCondition
import com.aconno.acnsensa.domain.ifttt.outcome.Outcome
import com.aconno.acnsensa.domain.interactor.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toInt
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ActionViewModel(
    application: Application,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val updateActionUseCase: UpdateActionUseCase,
    private val deleteActionUseCase: DeleteActionUseCase
) : AndroidViewModel(application) {

    private var id = 0L
    private val nameLiveData = MutableLiveData<String>()
    private val conditionLiveData = MutableLiveData<Condition>()
    private val outcomeLiveData = MutableLiveData<Outcome>()

    fun getAction(id: Long) {
        getActionByIdUseCase.execute(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onActionFound(it) }, { Timber.e("Failed to get action with id: $id") })
    }

    private fun onActionFound(action: Action) {
        id = action.id
        nameLiveData.value = action.name
        conditionLiveData.value = action.condition
        outcomeLiveData.value = action.outcome
    }

    fun setName(name: String) {
        nameLiveData.value = name
    }

    fun setCondition(
        sensorType: SensorTypeSingle,
        constraintType: String,
        constraintValue: String
    ) {
        val valueFloat = constraintValue.toFloatOrNull()
        if (valueFloat == null) {
            Timber.d("Constraint value is not valid float value: $constraintValue")
        } else {
            when (constraintType) {
                "<" -> conditionLiveData.value =
                        LimitCondition(sensorType.toInt(), valueFloat, LimitCondition.LESS_THAN)
                ">" -> conditionLiveData.value =
                        LimitCondition(sensorType.toInt(), valueFloat, LimitCondition.MORE_THAN)
                else -> Timber.d("Constraint type is not valid constraint type: $constraintType")
            }
        }
    }

    fun setOutcome(outcomeType: String, message: String, phoneNumber: String) {
        val parameters =
            mapOf(Pair(Outcome.TEXT_MESSAGE, message), Pair(Outcome.PHONE_NUMBER, phoneNumber))
        when (outcomeType) {
            getApplication<Application>().getString(R.string.phone_notification) -> outcomeLiveData.value =
                    Outcome(parameters, Outcome.OUTCOME_TYPE_NOTIFICATION)
            getApplication<Application>().getString(R.string.sms_message) -> outcomeLiveData.value =
                    Outcome(parameters, Outcome.OUTCOME_TYPE_SMS)
            getApplication<Application>().getString(R.string.vibration) -> outcomeLiveData.value =
                    Outcome(parameters, Outcome.OUTCOME_TYPE_VIBRATION)
            getApplication<Application>().getString(R.string.text_to_speech) -> outcomeLiveData.value =
                    Outcome(parameters, Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH)
            else -> Timber.d("Outcome type is not valid: $outcomeType")
        }
    }

    fun save() {
        val name = nameLiveData.value
        val condition = conditionLiveData.value
        val outcome = outcomeLiveData.value
        if (name != null && condition != null && outcome != null) {
            val action = GeneralAction(id, name, condition, outcome)
            updateActionUseCase.execute(action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("Save succeeded, action id: $id") },
                    { Timber.d("Save failed, action id: $id") })
        } else {
            Timber.d("Save failed, name: $name, condition: $condition, outcome: $outcome")
        }
    }

    fun delete() {
        val name = nameLiveData.value
        val condition = conditionLiveData.value
        val outcome = outcomeLiveData.value
        if (name != null && condition != null && outcome != null) {
            val action = GeneralAction(id, name, condition, outcome)
            deleteActionUseCase.execute(action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.d("Delete succeeded, action id: $id") },
                    { Timber.d("Delete failed, action id: $id") })
        } else {
            Timber.d("Delete failed, name: $name, condition: $condition, outcome: $outcome")
        }
    }
}
