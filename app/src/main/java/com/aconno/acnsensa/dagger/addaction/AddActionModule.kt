package com.aconno.acnsensa.dagger.addaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.ActionsRespository
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.ui.AddActionActivity
import com.aconno.acnsensa.viewmodel.ActionViewModel
import com.aconno.acnsensa.viewmodel.factory.ActionViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class AddActionModule(private val addActionActivity: AddActionActivity) {

    @Provides
    @AddActionActivityScope
    fun provideActionViewModel(
        actionViewModelFactory: ActionViewModelFactory
    ) = ViewModelProviders.of(addActionActivity, actionViewModelFactory)
        .get(ActionViewModel::class.java)

    @Provides
    @AddActionActivityScope
    fun provideActionViewModelFactory(addActionUseCase: AddActionUseCase) =
        ActionViewModelFactory(addActionUseCase)

    @Provides
    @AddActionActivityScope
    fun provideAddActionUseCase(actionsRepository: ActionsRespository): AddActionUseCase {
        return AddActionUseCase(actionsRepository)
    }


}