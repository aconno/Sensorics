package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.viewmodel.DeviceViewModel

class DeviceListViewModelFactory(
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceViewModel(getSavedDevicesUseCase)
        return getViewModel(viewModel, modelClass)
    }
}