package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class SensorListViewModel(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : ViewModel() {

    private val sensorValuesLiveData: MutableLiveData<Map<String, Number>> = MutableLiveData()

    private var disposable: Disposable? = null

    fun setMacAddress(macAddress: String) {
        disposable?.dispose()
        disposable = readingsStream
            .observeOn(AndroidSchedulers.mainThread())
            .concatMap { filterByMacUseCase.execute(it, macAddress).toFlowable() }
            .subscribe { processSensorValues(it) }
    }

    private fun processSensorValues(values: List<Reading>) {
        sensorValuesLiveData.value = values.associateBy({ it.name }, { it.value })
    }

    fun getSensorValuesLiveData(): MutableLiveData<Map<String, Number>> {
        return sensorValuesLiveData
    }
}