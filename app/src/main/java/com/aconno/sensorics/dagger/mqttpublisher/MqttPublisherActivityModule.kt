package com.aconno.sensorics.dagger.mqttpublisher

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import com.aconno.sensorics.model.mapper.MqttPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import com.aconno.sensorics.viewmodel.MqttPublisherViewModel
import com.aconno.sensorics.viewmodel.factory.MqttPublisherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MqttPublisherActivityModule {

    @Provides
    @MqttPublisherActivityScope
    fun provideMqttPublisherViewModel(
        mqttPublisherActivity: MqttPublisherActivity,
        mqttPublisherViewModelFactory: MqttPublisherViewModelFactory
    ) = ViewModelProviders.of(mqttPublisherActivity, mqttPublisherViewModelFactory)
        .get(MqttPublisherViewModel::class.java)

    @Provides
    @MqttPublisherActivityScope
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


}