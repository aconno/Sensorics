package com.aconno.sensorics.dagger.mqttpublisher

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.settings.publishers.selectpublish.MqttPublisherActivity
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [MqttPublisherModule::class])
@MqttPublisherScope
interface MqttPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(mqttPublisherActivity: MqttPublisherActivity)
}