package com.aconno.acnsensa.dagger.deviceselect

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.ui.settings.publishers.DeviceSelectFragment
import com.aconno.acnsensa.viewmodel.DeviceSelectViewModel
import com.aconno.acnsensa.viewmodel.factory.DeviceSelectViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class DeviceSelectModule(private val deviceSelectFragment: DeviceSelectFragment) {

    @Provides
    @DeviceSelectScope
    fun provideGoogleCloudPublisherViewModel(
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

    @Provides
    @DeviceSelectScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceSelectScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithRESTPublishUseCase {
        return GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @DeviceSelectScope
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }
}