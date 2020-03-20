package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.GetDevicesConnectedWithPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel

class DeviceSelectViewModelFactory(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesConnectedWithPublishUseCase: GetDevicesConnectedWithPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : BaseViewModelFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceSelectViewModel(
            getSavedDevicesMaybeUseCase,
            getDevicesConnectedWithPublishUseCase,
            deviceRelationModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }

}
