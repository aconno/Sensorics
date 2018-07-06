package com.aconno.sensorics.dagger.actionedit

import com.aconno.sensorics.dagger.actionlist.ActionListComponent
import com.aconno.sensorics.ui.actions.EditActionActivity
import dagger.Component

@Component(dependencies = [ActionListComponent::class], modules = [EditActionModule::class])
@EditActionActivityScope
interface EditActionComponent {

    fun inject(editActionActivity: EditActionActivity)
}