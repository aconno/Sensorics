package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

/**
 * @author aconno
 */
class SensorListViewModel(
    private val scanResults: Flowable<ScanResult>,
    private val filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
    private val filterByMacAddressUseCase: FilterByMacAddressUseCase,
    private val deserializeScanResultUseCase: DeserializeScanResultUseCase
) : ViewModel() {

    private val sensorValuesLiveData: MutableLiveData<Map<String, Number>> = MutableLiveData()

    private var disposable: Disposable? = null

    fun setMacAddress(macAddress: String) {
        disposable?.dispose()
        disposable = scanResults.concatMap { filterAdvertisementsUseCase.execute(it).toFlowable() }
            .concatMap { filterByMacAddressUseCase.execute(it, macAddress).toFlowable() }
            .concatMap { deserializeScanResultUseCase.execute(it).toFlowable() }
            .subscribe { processSensorValues(it) }
    }

    private fun processSensorValues(values: Map<String, Number>) {
        sensorValuesLiveData.value = values
    }

    fun getSensorValuesLiveData(): MutableLiveData<Map<String, Number>> {
        return sensorValuesLiveData
    }
}