package com.aconno.acnsensa.dagger.addpublish

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.AddPublishActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [AddPublishModule::class])
@AddPublishActivityScope
interface AddPublishComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(addPublishActivity: AddPublishActivity)
}



