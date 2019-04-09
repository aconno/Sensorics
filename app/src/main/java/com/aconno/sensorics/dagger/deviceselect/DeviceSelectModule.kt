package com.aconno.sensorics.dagger.deviceselect

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithRestPublishUseCase
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
        getDevicesThatConnectedWithRestPublishUseCase: GetDevicesThatConnectedWithRestPublishUseCase,
        getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper
    ) = DeviceSelectViewModelFactory(
        getSavedDevicesMaybeUseCase,
        getDevicesThatConnectedWithGooglePublishUseCase,
        getDevicesThatConnectedWithRestPublishUseCase,
        getDevicesThatConnectedWithMqttPublishUseCase,
        deviceRelationModelMapper
    )
}