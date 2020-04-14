package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceGroupUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDeviceGroupsUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceGroupUseCase
import com.aconno.sensorics.viewmodel.DeviceGroupViewModel

class DeviceGroupViewModelFactory(
    private val saveDeviceGroupUseCase: SaveDeviceGroupUseCase,
    private val getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
    private val deleteDeviceGroupsUseCase: DeleteDeviceGroupUseCase
)  : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceGroupViewModel(
            saveDeviceGroupUseCase,
            getSavedDeviceGroupsUseCase,
            deleteDeviceGroupsUseCase
        )
        return getViewModel(viewModel, modelClass)
    }


}