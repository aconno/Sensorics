package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel

class DeviceSelectViewModelFactory(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val getDevicesThatConnectedWithRestPublishUseCase: GetDevicesThatConnectedWithRestPublishUseCase,
    private val getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : BaseViewModelFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceSelectViewModel(
            getSavedDevicesMaybeUseCase,
            getDevicesThatConnectedWithGooglePublishUseCase,
            getDevicesThatConnectedWithRestPublishUseCase,
            getDevicesThatConnectedWithMqttPublishUseCase,
            deviceRelationModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }

}
