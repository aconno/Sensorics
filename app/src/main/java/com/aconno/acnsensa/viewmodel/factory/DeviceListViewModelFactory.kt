package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.acnsensa.viewmodel.DeviceViewModel

class DeviceListViewModelFactory(
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDevicesUseCase: SaveDeviceUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceViewModel(getSavedDevicesUseCase, saveDevicesUseCase)
        return getViewModel(viewModel, modelClass)
    }
}