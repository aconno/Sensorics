package com.aconno.acnsensa

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable

/**
 * @author aconno
 */
class SensorListViewModel(
    private val bluetooth: Bluetooth,
    private val filterAdvertisementsUseCase: MaybeUseCaseWithParameter<ScanResult, ScanResult>,
    private val sensorValuesUseCase: SingleUseCaseWithParameter<Map<String, Number>, ScanResult>
) : ViewModel() {

    private val result: MutableLiveData<Map<String, Number>> = MutableLiveData()


    fun subscribe() {
        val observable: Flowable<ScanResult> = bluetooth.getScanResults()
        observable.subscribe { scanResult ->
            filterAdvertisementsUseCase
                .execute(scanResult)
                .subscribe { filteredScanResult ->
                    sensorValuesUseCase
                        .execute(filteredScanResult)
                        .subscribe { sensorValues -> processSensorValues(sensorValues) }
                }
        }
    }

    fun startScanning() {
        bluetooth.startScanning()
    }

    fun stopScanning() {
        bluetooth.stopScanning()
    }

    private fun processSensorValues(values: Map<String, Number>) {
        result.value = values
    }

    fun getResult(): MutableLiveData<Map<String, Number>> {
        return result
    }


}