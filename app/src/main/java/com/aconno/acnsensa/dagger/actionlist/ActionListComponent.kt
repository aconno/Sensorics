package com.aconno.acnsensa.dagger.actionlist

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.ActionListFragment
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [ActionListModule::class])
@ActionListScope
interface ActionListComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(actionsListFragment: ActionListFragment)
}

