package com.aconno.sensorics.dagger.publish

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.settings.publishers.PublishListFragment
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [PublishListModule::class])
@PublishListScope
interface PublishListComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(publishListFragment: PublishListFragment)
}

