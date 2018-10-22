package com.aconno.sensorics.dagger.deviceselect

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel
import com.aconno.sensorics.viewmodel.factory.DeviceSelectViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class DeviceSelectModule {

    @Provides
    @DeviceSelectScope
    fun provideGoogleCloudPublisherViewModel(
        deviceSelectFragment: DeviceSelectFragment,
        deviceSelectViewModelFactory: DeviceSelectViewModelFactory
    ) = ViewModelProviders.of(deviceSelectFragment, deviceSelectViewModelFactory)
        .get(DeviceSelectViewModel::class.java)

    @Provides
    @DeviceSelectScope
    fun provideDeviceSelectViewModelFactory(
        getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
        getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
        getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
        getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper
    ) = DeviceSelectViewModelFactory(
        getSavedDevicesMaybeUseCase,
        getDevicesThatConnectedWithGooglePublishUseCase,
        getDevicesThatConnectedWithRESTPublishUseCase,
        getDevicesThatConnectedWithMqttPublishUseCase,
        deviceRelationModelMapper
    )
}