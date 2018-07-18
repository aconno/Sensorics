package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.GeneralAction
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Flowable

class ActionDetailsViewModel(
    savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val addActionUseCase: AddActionUseCase
) : ViewModel() {

    private var id = 0L

    private val devicesLiveData = MutableLiveData<List<Device>>()
    fun getDevicesLiveData(): LiveData<List<Device>> = devicesLiveData

    private val devicesStreamDisposable = savedDevicesStream.subscribe {
        devicesLiveData.postValue(it)
    }

    private val selectedDeviceLiveData = MutableLiveData<Device>()
    fun getSelectedDeviceLiveData(): LiveData<Device> = selectedDeviceLiveData

    private val readingTypesLiveData = MutableLiveData<List<String>>()
    fun getReadingTypesLiveData(): LiveData<List<String>> = readingTypesLiveData

    fun setSelectedDevice(device: Device) {
        selectedDeviceLiveData.value = device
        readingTypesLiveData.value = formatMatcher.getReadingTypes(device.name)
    }

    private val conditionLiveData = MutableLiveData<Condition>()
    fun getConditionLiveData(): LiveData<Condition> = conditionLiveData

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

    private val outcomeLiveData = MutableLiveData<String>()
    fun getOutcomeLiveData(): LiveData<String> = outcomeLiveData

    fun setOutcome(outcome: String) {
        outcomeLiveData.value = outcome
    }

    fun saveAction(name: String, message: String, phoneNumber: String = ""): Completable {
        val macAddress = selectedDeviceLiveData.value?.macAddress
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
        return if (macAddress == null || condition == null) {
            Completable.error(IllegalArgumentException("Invalid parameters, mac address: $macAddress, condition: $condition"))
        } else {
            val action = GeneralAction(
                id,
                name,
                macAddress,
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