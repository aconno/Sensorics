package com.aconno.sensorics.dagger.mqttvirtualscanningsource

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.ui.settings.virtualscanningsources.MqttVirtualScanningSourceActivity
import com.aconno.sensorics.viewmodel.MqttVirtualScanningSourceViewModel
import com.aconno.sensorics.viewmodel.factory.MqttVirtualScanningSourceViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MqttVirtualScanningSourceActivityModule {

    @Provides
    @MqttVirtualScanningSourceActivityScope
    fun provideMqttVirtualScanningSourceViewModel(
            mqttVirtualScanningSourceActivity: MqttVirtualScanningSourceActivity,
            mqttVirtualScanningSourceViewModelFactory: MqttVirtualScanningSourceViewModelFactory
    ) = ViewModelProviders.of(mqttVirtualScanningSourceActivity, mqttVirtualScanningSourceViewModelFactory)
                    .get(MqttVirtualScanningSourceViewModel::class.java)

    @Provides
    @MqttVirtualScanningSourceActivityScope
    fun provideMqttVirtualScanningSourceViewModelFactory(
            addMqttVirtualScanningSourceUseCase: AddMqttVirtualScanningSourceUseCase,
            mqttVirtualScanningSourceModelDataMapper: MqttVirtualScanningSourceModelDataMapper
    ) = MqttVirtualScanningSourceViewModelFactory(
            addMqttVirtualScanningSourceUseCase,
            mqttVirtualScanningSourceModelDataMapper
    )
}