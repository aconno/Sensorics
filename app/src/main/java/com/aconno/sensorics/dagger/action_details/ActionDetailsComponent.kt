package com.aconno.sensorics.dagger.action_details

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [ActionDetailsModule::class])
@ActionDetailsActivityScope
interface ActionDetailsComponent {

    fun inject(actionDetailsActivity: ActionDetailsActivity)
}