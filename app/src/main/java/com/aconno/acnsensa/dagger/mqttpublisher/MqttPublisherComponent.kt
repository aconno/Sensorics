package com.aconno.acnsensa.dagger.mqttpublisher

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.selectpublish.MqttPublisherActivity
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [MqttPublisherModule::class])
@MqttPublisherScope
interface MqttPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(mqttPublisherActivity: MqttPublisherActivity)
}