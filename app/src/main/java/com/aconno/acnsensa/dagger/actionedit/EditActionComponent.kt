package com.aconno.acnsensa.dagger.actionedit

import com.aconno.acnsensa.dagger.actionlist.ActionListComponent
import com.aconno.acnsensa.ui.actions.EditActionActivity
import dagger.Component

@Component(dependencies = [ActionListComponent::class], modules = [EditActionModule::class])
@EditActionActivityScope
interface EditActionComponent {

    fun inject(editActionActivity: EditActionActivity)
}