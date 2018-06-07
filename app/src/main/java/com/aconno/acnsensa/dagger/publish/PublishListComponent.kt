package com.aconno.acnsensa.dagger.publish

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.PublishListFragment
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

