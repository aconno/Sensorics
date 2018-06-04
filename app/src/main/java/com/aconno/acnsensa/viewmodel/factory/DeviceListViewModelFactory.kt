package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetAllDevicesUseCase
import com.aconno.acnsensa.viewmodel.DeviceListViewModel

class DeviceListViewModelFactory(
    private val getAllDevicesUseCase: GetAllDevicesUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceListViewModel(getAllDevicesUseCase)
        return getViewModel(viewModel, modelClass)
    }
}