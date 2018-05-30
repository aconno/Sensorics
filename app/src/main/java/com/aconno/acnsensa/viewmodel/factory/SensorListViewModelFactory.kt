package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.viewmodel.SensorListViewModel
import io.reactivex.Flowable

/**
 * @author aconno
 */
class SensorListViewModelFactory(
    private val scanResults: Flowable<ScanResult>,
    private val filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
    private val filterByMacAddressUseCase: FilterByMacAddressUseCase,
    private val deserializeScanResultUseCase: DeserializeScanResultUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SensorListViewModel(
            scanResults,
            filterAdvertisementsUseCase,
            filterByMacAddressUseCase,
            deserializeScanResultUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}