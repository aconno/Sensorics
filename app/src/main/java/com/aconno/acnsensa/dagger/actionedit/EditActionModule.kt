package com.aconno.acnsensa.dagger.actionedit

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.ui.actions.EditActionActivity
import com.aconno.acnsensa.viewmodel.ActionViewModel
import com.aconno.acnsensa.viewmodel.factory.ActionViewModelFactory
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
        return GetActionByIdUseCase(actionsRepository)
    }

    @Provides
    @EditActionActivityScope
    fun provideUpdateActionUseCase(actionsRepository: ActionsRepository): UpdateActionUseCase {
        return UpdateActionUseCase(actionsRepository)
    }

    @Provides
    @EditActionActivityScope
    fun providesDeleteActionUseCase(actionsRepository: ActionsRepository): DeleteActionUseCase {
        return DeleteActionUseCase(actionsRepository)
    }
}