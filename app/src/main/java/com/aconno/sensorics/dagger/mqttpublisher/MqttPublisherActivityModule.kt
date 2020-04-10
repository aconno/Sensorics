package com.aconno.sensorics.dagger.mqttpublisher

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
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
    ): MqttPublisherViewModel = ViewModelProvider(
        mqttPublisherActivity,
        mqttPublisherViewModelFactory
    ).get(MqttPublisherViewModel::class.java)

    @Provides
    @MqttPublisherActivityScope
    fun provideMqttPublisherViewModelFactory(
        addAnyPublishUseCase: AddAnyPublishUseCase,
        getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
        savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
        mqttPublishModelDataMapper: MqttPublishModelDataMapper
    ) = MqttPublisherViewModelFactory(
        addAnyPublishUseCase,
        getMqttPublishByIdUseCase,
        savePublishDeviceJoinUseCase,
        deletePublishDeviceJoinUseCase,
        mqttPublishModelDataMapper
    )


}