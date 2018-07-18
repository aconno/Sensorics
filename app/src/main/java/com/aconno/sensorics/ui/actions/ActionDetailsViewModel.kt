package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ActionDetailsViewModel(
    savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val addActionUseCase: AddActionUseCase
) : ViewModel() {

    private var id = 0L

    private val nameLiveData = MutableLiveData<String>()
    private val devicesLiveData = MutableLiveData<List<Device>>()
    private val selectedDeviceLiveData = MutableLiveData<Device>()
    private val readingTypesLiveData = MutableLiveData<List<String>>()
    private val conditionLiveData = MutableLiveData<Condition>()
    private val outcomeLiveData = MutableLiveData<String>()
    private val messageLiveData = MutableLiveData<String>()
    private val phoneNumberLiveData = MutableLiveData<String>()

    fun getNameLiveData(): LiveData<String> = nameLiveData
    fun getDevicesLiveData(): LiveData<List<Device>> = devicesLiveData
    fun getSelectedDeviceLiveData(): LiveData<Device> = selectedDeviceLiveData
    fun getReadingTypesLiveData(): LiveData<List<String>> = readingTypesLiveData
    fun getConditionLiveData(): LiveData<Condition> = conditionLiveData
    fun getOutcomeLiveData(): LiveData<String> = outcomeLiveData
    fun getMessageLiveData(): LiveData<String> = messageLiveData
    fun getPhoneNumberLiveData(): LiveData<String> = phoneNumberLiveData

    private val devicesStreamDisposable = savedDevicesStream.subscribe {
        devicesLiveData.postValue(it)
    }

    fun setActionId(actionId: Long) {
        id = actionId
        getActionByIdUseCase.execute(actionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { action ->
                nameLiveData.value = action.name
                val device = Device("", "", action.device.macAddress)
                setSelectedDevice(device)
                conditionLiveData.value = action.condition
                outcomeLiveData.value = when (action.outcome.type) {
                    Outcome.OUTCOME_TYPE_NOTIFICATION -> "Notification"
                    Outcome.OUTCOME_TYPE_SMS -> "SMS"
                    Outcome.OUTCOME_TYPE_VIBRATION -> "Vibration"
                    Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> "Text to speech"
                    else -> throw IllegalArgumentException("Invalid outcome type: ${action.outcome.type}")
                }
                val message = action.outcome.parameters[Outcome.TEXT_MESSAGE]
                message?.let { messageLiveData.value = it }
                val phoneNumber = action.outcome.parameters[Outcome.PHONE_NUMBER]
                phoneNumber?.let { phoneNumberLiveData.value = it }
            }
    }

    fun setSelectedDevice(device: Device) {
        selectedDeviceLiveData.value = device
        readingTypesLiveData.value = formatMatcher.getReadingTypes(device.name)
    }

    fun setCondition(readingType: String, constraintType: String, limitValue: String) {
        val condition = LimitCondition(
            readingType,
            limitValue.toFloat(),
            when (constraintType) {
                "<" -> LimitCondition.LESS_THAN
                ">" -> LimitCondition.MORE_THAN
                else -> throw IllegalArgumentException("Invalid constraint type: $constraintType")
            }
        )
        conditionLiveData.value = condition
    }

    fun setOutcome(outcome: String) {
        outcomeLiveData.value = outcome
    }

    fun saveAction(name: String, message: String, phoneNumber: String = ""): Completable {
        val device = selectedDeviceLiveData.value
        val condition = conditionLiveData.value
        val parameters = hashMapOf<String, String>()
        parameters[Outcome.PHONE_NUMBER] = phoneNumber
        parameters[Outcome.TEXT_MESSAGE] = message
        val outcome = when (outcomeLiveData.value) {
            "Notification" -> Outcome(parameters, Outcome.OUTCOME_TYPE_NOTIFICATION)
            "SMS" -> Outcome(parameters, Outcome.OUTCOME_TYPE_SMS)
            "Vibration" -> Outcome(parameters, Outcome.OUTCOME_TYPE_VIBRATION)
            "Text to speech" -> Outcome(parameters, Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH)
            else -> return Completable.error(IllegalArgumentException("Invalid outcome: ${outcomeLiveData.value}"))
        }
        return if (device == null || condition == null) {
            Completable.error(IllegalArgumentException("Invalid parameters, device: $device, condition: $condition"))
        } else {
            val action = GeneralAction(
                id,
                name,
                device,
                condition,
                outcome
            )
            addActionUseCase.execute(action)
        }
    }

    override fun onCleared() {
        super.onCleared()
        devicesStreamDisposable.dispose()
    }
}