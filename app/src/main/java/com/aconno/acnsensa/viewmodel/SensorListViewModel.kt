package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable

/**
 * @author aconno
 */
class SensorListViewModel(
    private val sensorValues: Flowable<Map<String, Number>>
) : ViewModel() {

    private val sensorValuesLiveData: MutableLiveData<Map<String, Number>> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {

        sensorValues.subscribe { processSensorValues(it) }
    }

    private fun processSensorValues(values: Map<String, Number>) {
        sensorValuesLiveData.value = values
    }

    fun getSensorValuesLiveData(): MutableLiveData<Map<String, Number>> {
        return sensorValuesLiveData
    }
}