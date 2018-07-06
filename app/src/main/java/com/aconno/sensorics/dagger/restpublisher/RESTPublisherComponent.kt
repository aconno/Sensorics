package com.aconno.sensorics.dagger.restpublisher

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.settings.publishers.selectpublish.RESTPublisherActivity
import dagger.Component

@RESTPublisherScope
@Component(dependencies = [AppComponent::class], modules = [RESTPublisherModule::class])
interface RESTPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(restPublisherActivity: RESTPublisherActivity)
}
