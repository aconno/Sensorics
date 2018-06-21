package com.aconno.acnsensa.dagger.mqttpublisher

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.MqttPublishRepository
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import com.aconno.acnsensa.model.mapper.MqttPublishModelDataMapper
import com.aconno.acnsensa.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.acnsensa.viewmodel.MqttPublisherViewModel
import com.aconno.acnsensa.viewmodel.factory.MqttPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MqttPublisherModule(private val mqttPublisherActivity: MqttPublisherActivity) {

    @Provides
    @MqttPublisherScope
    fun provideRestPublisherViewModel(
        mqttPublisherViewModelFactory: MqttPublisherViewModelFactory
    ) = ViewModelProviders.of(mqttPublisherActivity, mqttPublisherViewModelFactory)
        .get(MqttPublisherViewModel::class.java)

    @Provides
    @MqttPublisherScope
    fun provideMqttPublisherViewModelFactory(
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
        deviceRelationModelMapper: DeviceRelationModelMapper,
        addMqttPublishUseCase: AddMqttPublishUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        devicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase
    ) = MqttPublisherViewModelFactory(
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        savedDevicesMaybeUseCase,
        deviceRelationModelMapper,
        addMqttPublishUseCase,
        mqttPublishModelDataMapper,
        devicesThatConnectedWithMqttPublishUseCase
    )

    @Provides
    @MqttPublisherScope
    fun provideSavePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): SavePublishDeviceJoinUseCase {
        return SavePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @MqttPublisherScope
    fun provideDeletePublishDeviceJoinUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): DeletePublishDeviceJoinUseCase {
        return DeletePublishDeviceJoinUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @MqttPublisherScope
    fun provideGetSavedDevicesMaybeUseCase(deviceRepository: DeviceRepository): GetSavedDevicesMaybeUseCase {
        return GetSavedDevicesMaybeUseCase(deviceRepository)
    }

    @Provides
    @MqttPublisherScope
    fun provideAddMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): AddMqttPublishUseCase {
        return AddMqttPublishUseCase(mqttPublishRepository)
    }

    @Provides
    @MqttPublisherScope
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }


}