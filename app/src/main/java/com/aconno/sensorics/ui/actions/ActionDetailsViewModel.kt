package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Flowable

class ActionDetailsViewModel(
    savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher
) : ViewModel() {

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

    fun saveAction(name: String, message: String, phoneNumber: String = "") {
        //TODO: Save action
    }

    override fun onCleared() {
        super.onCleared()
        devicesStreamDisposable.dispose()
    }
}