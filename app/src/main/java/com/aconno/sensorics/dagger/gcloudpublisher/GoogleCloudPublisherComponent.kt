package com.aconno.sensorics.dagger.gcloudpublisher

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.settings.publishers.selectpublish.GoogleCloudPublisherActivity
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [GoogleCloudPublisherModule::class])
@GoogleCloudPublisherScope
interface GoogleCloudPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(googleCloudPublisherActivity: GoogleCloudPublisherActivity)
}
