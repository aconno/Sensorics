package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel

class DeviceSelectViewModelFactory(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val getDevicesThatConnectedWithRestPublishUseCase: GetDevicesThatConnectedWithRestPublishUseCase,
    private val getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
    private val getDevicesThatConnectedWithAzureMqttPublishUseCase: GetDevicesThatConnectedWithAzureMqttPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : BaseViewModelFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceSelectViewModel(
            getSavedDevicesMaybeUseCase,
            getDevicesThatConnectedWithGooglePublishUseCase,
            getDevicesThatConnectedWithRestPublishUseCase,
            getDevicesThatConnectedWithMqttPublishUseCase,
            getDevicesThatConnectedWithAzureMqttPublishUseCase,
            deviceRelationModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }

}
