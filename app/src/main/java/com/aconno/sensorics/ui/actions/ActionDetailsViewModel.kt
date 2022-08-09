package com.aconno.sensorics.ui.actions

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ActionDetailsViewModel(
    private val savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val addActionUseCase: AddActionUseCase,
    private val getIconUseCase: GetIconUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val actionLiveData = MutableLiveData<ActionBeingBuilt>()
    fun getActionLiveData(): LiveData<ActionBeingBuilt> = actionLiveData

    fun getDevices(): Single<List<Device>> {
        return savedDevicesStream.firstOrError()
    }

    fun setActionId(actionId: Long) {
        disposables.add(
            getActionByIdUseCase.execute(actionId)
                .subscribeOn(Schedulers.io())
                .subscribe { action ->
                    actionLiveData.postValue(
                        ActionBeingBuilt(
                            action.id,
                            action.name,
                            action.device,
                            action.condition,
                            action.outcome,
                            action.active,
                            action.timeFrom,
                            action.timeTo
                        )
                    )
                }
        )
    }

    fun setDevice(device: Device, name: String, message1: String, message2: String) {
        actionLiveData.value?.let { actionViewModel ->
            actionViewModel.name = name
            actionViewModel.device = device
            val outcome = actionViewModel.outcome
            if (outcome != null) {
                val parameters = hashMapOf<String, String>()
                parameters[Outcome.TEXT_MESSAGE] = message1
                parameters[Outcome.PHONE_NUMBER] = message2
                val newOutcome = Outcome(parameters, outcome.type)
                actionViewModel.outcome = newOutcome
            }
            actionLiveData.value = actionViewModel
        } ?: kotlin.run {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                device = device
            )
        }
    }

    fun setLimitCondition(
        limitCondition: LimitCondition,
        name: String,
        message1: String,
        message2: String
    ) {
        actionLiveData.value?.let { actionViewModel ->
            actionViewModel.name = name
            actionViewModel.condition = limitCondition
            val outcome = actionViewModel.outcome
            if (outcome != null) {
                val parameters = hashMapOf<String, String>()
                parameters[Outcome.TEXT_MESSAGE] = message1
                parameters[Outcome.PHONE_NUMBER] = message2
                val newOutcome = Outcome(parameters, outcome.type)
                actionViewModel.outcome = newOutcome
            }
            actionLiveData.value = actionViewModel
        } ?: kotlin.run {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                condition = limitCondition
            )
        }
    }

    fun clearCondition(name: String, message1: String, message2: String) {
        actionLiveData.value?.let { actionViewModel ->
            actionViewModel.name = name
            actionViewModel.condition = null
            val outcome = actionViewModel.outcome
            if (outcome != null) {
                val parameters = hashMapOf<String, String>()
                parameters[Outcome.TEXT_MESSAGE] = message1
                parameters[Outcome.PHONE_NUMBER] = message2
                val newOutcome = Outcome(parameters, outcome.type)
                actionViewModel.outcome = newOutcome
            }
            actionLiveData.value = actionViewModel
        } ?: kotlin.run {
            actionLiveData.value = ActionBeingBuilt(
                name = name
            )
        }
    }

    fun setOutcome(outcomeType: Int, message1: String, message2: String, name: String) {
        val parameters = hashMapOf<String, String>()
        parameters[Outcome.TEXT_MESSAGE] = message1
        parameters[Outcome.PHONE_NUMBER] = message2
        val outcome = Outcome(parameters, outcomeType)
        actionLiveData.value?.let { actionViewModel ->
            actionViewModel.name = name
            actionViewModel.outcome = outcome
            actionLiveData.value = actionViewModel
        } ?: kotlin.run {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                outcome = outcome
            )
        }
    }

    fun setActive(name: String, active: Boolean) {
        actionLiveData.value?.let { actionViewModel ->
            actionViewModel.name = name
            actionViewModel.active = active
            actionLiveData.value = actionViewModel
        } ?: kotlin.run {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                active = active
            )
        }
    }

    fun setTimeFrom(name: String, timeFrom: Int) {
        val actionViewModel = actionLiveData.value
        if (actionViewModel == null) {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                timeFrom = timeFrom
            )
        } else {
            actionViewModel.name = name
            actionViewModel.timeFrom = timeFrom
            // TODO: Re-enable if you have issues because of the way that callback works this
            //  gets called a lot so I disabled it and it shouldn't really affect anything
//            actionLiveData.value = actionViewModel
        }
    }

    fun setTimeTo(name: String, timeTo: Int) {
        val actionViewModel = actionLiveData.value
        if (actionViewModel == null) {
            actionLiveData.value = ActionBeingBuilt(
                name = name,
                timeTo = timeTo
            )
        } else {
            actionViewModel.name = name
            actionViewModel.timeTo = timeTo
            // TODO: Re-enable if you have issues because of the way that callback works this
            //  gets called a lot so I disabled it and it shouldn't really affect anything
//            actionLiveData.value = actionViewModel
        }
    }

    fun getReadingTypes(device: Device): List<String> {
        return formatMatcher.getReadingTypes(device.name)
    }

    fun saveAction(
        application: Application,
        name: String,
        message1: String,
        message2: String
    ): Completable {
        val id = actionLiveData.value?.id ?: 0L
        if (name.isBlank()) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_action_name_blank
            )
        }
        val device = actionLiveData.value?.device
        val condition = actionLiveData.value?.condition
        val outcome = actionLiveData.value?.outcome
        val active = actionLiveData.value?.active
        val timeFrom = actionLiveData.value?.timeFrom
        val timeTo = actionLiveData.value?.timeTo
        if (device == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_action_device_not_selected
            )
        }
        if (condition == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_action_condition_not_selected
            )
        }
        if (outcome == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_action_outcome_not_selected
            )
        }
        if (active == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_action_active_state_not_selected
            )
        }
        if (timeFrom == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_time_from_not_selected
            )
        }
        if (timeTo == null) {
            return getCompletableIllegalArgumentError(
                application,
                R.string.message_time_to_not_selected
            )
        }
        val parameters = hashMapOf<String, String>()

        parameters[Outcome.TEXT_MESSAGE] = message1
        parameters[Outcome.PHONE_NUMBER] = message2
        val newOutcome = Outcome(parameters, outcome.type)
        val action = GeneralAction(
            id,
            name,
            device,
            condition,
            newOutcome,
            active,
            timeFrom,
            timeTo
        )

        return Completable.fromSingle(addActionUseCase.execute(action))
    }

    private fun getCompletableIllegalArgumentError(
        application: Application,
        messageResourceId: Int
    ): Completable {
        val message = application.getString(messageResourceId)
        return Completable.error(IllegalArgumentException(message))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    inner class ActionBeingBuilt(
        var id: Long = 0L,
        var name: String = "",
        var device: Device? = null,
        var condition: Condition? = null,
        var outcome: Outcome? = null,
        var active: Boolean = true,
        var timeFrom: Int = 0,
        var timeTo: Int = 0
    )

    fun getIconPath(deviceName: String): String? {
        return getIconUseCase.execute(deviceName)
    }
}
