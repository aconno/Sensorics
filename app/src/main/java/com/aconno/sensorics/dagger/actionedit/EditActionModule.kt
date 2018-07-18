package com.aconno.sensorics.dagger.actionedit

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.UpdateActionUseCase
import com.aconno.sensorics.ui.actions.EditActionActivity
import com.aconno.sensorics.viewmodel.ActionViewModel
import com.aconno.sensorics.viewmodel.factory.ActionViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class EditActionModule(private val editActionActivity: EditActionActivity) {

    @Provides
    @EditActionActivityScope
    fun provideActionViewModel(actionViewModelFactory: ActionViewModelFactory): ActionViewModel {
        return ViewModelProviders.of(editActionActivity, actionViewModelFactory)
            .get(ActionViewModel::class.java)
    }

    @Provides
    @EditActionActivityScope
    fun provideActionViewModelFactory(
        getActionByIdUseCase: GetActionByIdUseCase,
        updateActionUseCase: UpdateActionUseCase,
        deleteActionUseCase: DeleteActionUseCase
    ): ActionViewModelFactory {
        return ActionViewModelFactory(
            editActionActivity.application,
            getActionByIdUseCase,
            updateActionUseCase,
            deleteActionUseCase
        )
    }

    @Provides
    @EditActionActivityScope
    fun providesGetActionActionUseCase(actionsRepository: ActionsRepository): GetActionByIdUseCase {
        return GetActionByIdUseCase(
            actionsRepository
        )
    }

    @Provides
    @EditActionActivityScope
    fun provideUpdateActionUseCase(actionsRepository: ActionsRepository): UpdateActionUseCase {
        return UpdateActionUseCase(
            actionsRepository
        )
    }

    @Provides
    @EditActionActivityScope
    fun providesDeleteActionUseCase(actionsRepository: ActionsRepository): DeleteActionUseCase {
        return DeleteActionUseCase(
            actionsRepository
        )
    }
}