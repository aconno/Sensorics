package com.aconno.acnsensa.dagger.actionlist

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.GetAllActionsUseCase
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
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
    fun provideActionOptionsViewModel() =
        ViewModelProviders.of(activity).get(ActionOptionsViewModel::class.java)
}