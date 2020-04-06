package com.aconno.sensorics.dagger.deviceselect

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.repository.GetDevicesConnectedWithPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.viewmodel.DeviceSelectViewModel
import com.aconno.sensorics.viewmodel.factory.DeviceSelectViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class DeviceSelectActivityModule {

    @Provides
    @DeviceSelectActivityScope
    fun provideGoogleCloudPublisherViewModel(
        deviceSelectFragment: DeviceSelectFragment,
        deviceSelectViewModelFactory: DeviceSelectViewModelFactory
    ) = ViewModelProviders.of(deviceSelectFragment, deviceSelectViewModelFactory)
        .get(DeviceSelectViewModel::class.java)

    @Provides
    @DeviceSelectActivityScope
    fun provideDeviceSelectViewModelFactory(
        getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
        getDevicesConnectedWithPublishUseCase: GetDevicesConnectedWithPublishUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper
    ) = DeviceSelectViewModelFactory(
        getSavedDevicesMaybeUseCase,
        getDevicesConnectedWithPublishUseCase,
        deviceRelationModelMapper
    )
}