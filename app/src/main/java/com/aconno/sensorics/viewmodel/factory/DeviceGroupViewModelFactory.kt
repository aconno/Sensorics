package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.viewmodel.DeviceGroupViewModel

class DeviceGroupViewModelFactory(
    private val saveDeviceGroupUseCase: SaveDeviceGroupUseCase,
    private val getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
    private val deleteDeviceGroupsUseCase: DeleteDeviceGroupUseCase,
    private val updateDeviceGroupsUseCase: UpdateDeviceGroupUseCase,
    private val saveDeviceGroupDeviceJoinUseCase: SaveDeviceGroupDeviceJoinUseCase,
    private val deleteDeviceGroupDeviceJoinUseCase: DeleteDeviceGroupDeviceJoinUseCase,
    private val getDevicesInDeviceGroupUseCase: GetDevicesInDeviceGroupUseCase,
    private val getDevicesBelongingSomeDeviceGroupUseCase: GetDevicesBelongingSomeDeviceGroupUseCase
)  : BaseViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = DeviceGroupViewModel(
            saveDeviceGroupUseCase,
            getSavedDeviceGroupsUseCase,
            deleteDeviceGroupsUseCase,
            updateDeviceGroupsUseCase,
            saveDeviceGroupDeviceJoinUseCase,
            deleteDeviceGroupDeviceJoinUseCase,
            getDevicesInDeviceGroupUseCase,
            getDevicesBelongingSomeDeviceGroupUseCase
        )
        return getViewModel(viewModel, modelClass)
    }


}