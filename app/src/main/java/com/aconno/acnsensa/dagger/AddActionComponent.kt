package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.ui.AddActionActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [AddActionModule::class])
@AddActionActivityScope
interface AddActionComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(addActionActivity: AddActionActivity)
}



