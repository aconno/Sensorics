package com.aconno.acnsensa.dagger.restpublisher

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.selectpublish.RESTPublisherActivity
import dagger.Component

@RESTPublisherScope
@Component(dependencies = [AppComponent::class], modules = [RESTPublisherModule::class])
interface RESTPublisherComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(restPublisherActivity: RESTPublisherActivity)
}
