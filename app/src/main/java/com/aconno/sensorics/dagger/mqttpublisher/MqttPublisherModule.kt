package com.aconno.sensorics.dagger.mqttpublisher

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.MqttPublisherViewModelFactory
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
        addMqttPublishUseCase: AddMqttPublishUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper
    ) = MqttPublisherViewModelFactory(
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        addMqttPublishUseCase,
        mqttPublishModelDataMapper
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
    fun provideAddMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): AddMqttPublishUseCase {
        return AddMqttPublishUseCase(mqttPublishRepository)
    }

    @Provides
    @MqttPublisherScope
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }
}