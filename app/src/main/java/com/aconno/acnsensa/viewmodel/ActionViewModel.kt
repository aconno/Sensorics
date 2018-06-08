package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
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
    val nameLiveData = MutableLiveData<String>()
    val deviceMacAddressLiveData = MutableLiveData<String>()
    val conditionLiveData = MutableLiveData<Condition>()
    val outcomeLiveData = MutableLiveData<Outcome>()

    fun getAction(id: Long) {
        getActionByIdUseCase.execute(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onActionFound(it) }, { Timber.e("Failed to get action with id: $id") })
    }

    private fun onActionFound(action: Action) {
        id = action.id
        nameLiveData.value = action.name
        deviceMacAddressLiveData.value = action.deviceMacAddress
        conditionLiveData.value = action.condition
        outcomeLiveData.value = action.outcome
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
                        LimitCondition(sensorType, valueFloat, LimitCondition.LESS_THAN)
                ">" -> conditionLiveData.value =
                        LimitCondition(sensorType, valueFloat, LimitCondition.MORE_THAN)
                else -> Timber.d("Constraint type is not valid constraint type: $constraintType")
            }
        }
    }

    fun save(
        name: String,
        deviceMacAddress: String,
        outcomeType: Int,
        message: String,
        phoneNumber: String
    ) {
        val condition = conditionLiveData.value
        val parameters =
            mapOf(Pair(Outcome.TEXT_MESSAGE, message), Pair(Outcome.PHONE_NUMBER, phoneNumber))
        val outcome = Outcome(parameters, outcomeType)
        if (condition != null) {
            val action = GeneralAction(id, name, deviceMacAddress, condition, outcome)
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
        val deviceMacAddress = deviceMacAddressLiveData.value
        val condition = conditionLiveData.value
        val outcome = outcomeLiveData.value
        if (name != null && deviceMacAddress != null && condition != null && outcome != null) {
            val action = GeneralAction(id, name, deviceMacAddress, condition, outcome)
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
