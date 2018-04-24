package com.aconno.acnsensa.dagger.actionlist

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllActionsUseCase
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import com.aconno.acnsensa.viewmodel.factory.ActionOptionsViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class ActionListModule(private val activity: AppCompatActivity) {

    @Provides
    @ActionListScope
    fun provideGetAllActionsUseCase(actionsRepository: ActionsRepository): GetAllActionsUseCase {
        return GetAllActionsUseCase(actionsRepository)
    }

    @Provides
    @ActionListScope
    fun provideActionOptionsViewModel(): ActionOptionsViewModel {
        val actionOptionsViewModelFactory = ActionOptionsViewModelFactory(activity.application)

        return ViewModelProviders.of(activity, actionOptionsViewModelFactory)
            .get(ActionOptionsViewModel::class.java)
    }
}