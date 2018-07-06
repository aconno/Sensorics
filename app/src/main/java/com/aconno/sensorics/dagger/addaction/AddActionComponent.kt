package com.aconno.sensorics.dagger.addaction

import com.aconno.sensorics.dagger.actionlist.ActionListComponent
import com.aconno.sensorics.ui.actions.AddActionActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [ActionListComponent::class], modules = [AddActionModule::class])
@AddActionActivityScope
interface AddActionComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(addActionActivity: AddActionActivity)
}



