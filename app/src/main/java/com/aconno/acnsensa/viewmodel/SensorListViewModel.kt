package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable

//TODO: This needs refactoring.
/**
 * @author aconno
 */
class SensorListViewModel(
    private val bluetooth: Bluetooth,
    private val filterAdvertisementsUseCase: MaybeUseCaseWithParameter<ScanResult, ScanResult>,
    private val sensorValuesUseCase: SingleUseCaseWithParameter<Map<String, Number>, ScanResult>
) : ViewModel() {

    private val result: MutableLiveData<Map<String, Number>> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {

        getSensorValuesFlowable().subscribe { processSensorValues(it) }
    }

    private fun getSensorValuesFlowable(): Flowable<Map<String, Number>> {
        val observable: Flowable<ScanResult> = bluetooth.getScanResults()
        return observable
            .concatMap { filterAdvertisementsUseCase.execute(it).toFlowable() }
            .concatMap { sensorValuesUseCase.execute(it).toFlowable() }
    }

    private fun processSensorValues(values: Map<String, Number>) {
        result.value = values
    }

    fun getResult(): MutableLiveData<Map<String, Number>> {
        return result
    }
}