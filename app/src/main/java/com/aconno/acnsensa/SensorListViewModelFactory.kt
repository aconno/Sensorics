package com.aconno.acnsensa

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase

/**
 * @author aconno
 */
class SensorListViewModelFactory(
    private val bluetooth: Bluetooth,
    private val filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
    private val sensorValuesUseCase: GetSensorValuesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") //Safe to suppress since as? casting is being used.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: T? =
            SensorListViewModel(bluetooth, filterAdvertisementsUseCase, sensorValuesUseCase) as? T
        viewModel?.let { return viewModel }

        throw IllegalArgumentException("Illegal cast for SensorListViewModel")
    }
}