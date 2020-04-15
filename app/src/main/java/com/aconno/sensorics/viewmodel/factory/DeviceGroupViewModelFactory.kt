package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.viewmodel.DeviceGroupViewModel

class DeviceGroupViewModelFactory(
    private val saveDeviceGroupUseCase: SaveDeviceGroupUseCase,
    private val getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
    private val deleteDeviceGroupsUseCase: DeleteDeviceGroupUseCase,
    private val saveDeviceGroupDeviceJoinUseCase: SaveDeviceGroupDeviceJoinUseCase,
    private val deleteDeviceGroupDeviceJoinUseCase: DeleteDeviceGroupDeviceJoinUseCase,
    private val getDevicesInDeviceGroupUseCase: GetDevicesInDeviceGroupUseCase
)  : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceGroupViewModel(
            saveDeviceGroupUseCase,
            getSavedDeviceGroupsUseCase,
            deleteDeviceGroupsUseCase,
            saveDeviceGroupDeviceJoinUseCase,
            deleteDeviceGroupDeviceJoinUseCase,
            getDevicesInDeviceGroupUseCase
        )
        return getViewModel(viewModel, modelClass)
    }


}