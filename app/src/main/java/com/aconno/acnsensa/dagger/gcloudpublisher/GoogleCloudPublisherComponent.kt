package com.aconno.acnsensa.dagger.gcloudpublisher

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.selectpublish.GoogleCloudPublisherActivity
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [GoogleCloudPublisherModule::class])
@GoogleCloudPublisherScope
interface GoogleCloudPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(googleCloudPublisherActivity: GoogleCloudPublisherActivity)
}
