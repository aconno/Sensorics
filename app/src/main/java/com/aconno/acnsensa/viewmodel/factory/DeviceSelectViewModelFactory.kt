package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.viewmodel.DeviceSelectViewModel
import io.reactivex.Flowable

class DeviceSelectViewModelFactory(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : BaseViewModelFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceSelectViewModel(
            getSavedDevicesMaybeUseCase,
            getDevicesThatConnectedWithGooglePublishUseCase,
            getDevicesThatConnectedWithRESTPublishUseCase,
            getDevicesThatConnectedWithMqttPublishUseCase,
            deviceRelationModelMapper
        )
        return getViewModel(viewModel, modelClass)
    }

}
