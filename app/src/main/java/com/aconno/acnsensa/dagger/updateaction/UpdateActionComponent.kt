package com.aconno.acnsensa.dagger.updateaction

import com.aconno.acnsensa.dagger.actionlist.ActionListComponent
import com.aconno.acnsensa.ui.UpdateActionActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [ActionListComponent::class], modules = [UpdateActionModule::class])
@UpdateActionActivityScope
interface UpdateActionComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(updateActionActivity: UpdateActionActivity)
}
