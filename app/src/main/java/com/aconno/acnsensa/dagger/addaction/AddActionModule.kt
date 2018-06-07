package com.aconno.acnsensa.dagger.addaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.interactor.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.ui.actions.AddActionActivity
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import com.aconno.acnsensa.viewmodel.factory.NewActionViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class AddActionModule(private val addActionActivity: AddActionActivity) {

    @Provides
    @AddActionActivityScope
    fun provideActionViewModel(
        newActionViewModelFactory: NewActionViewModelFactory
    ) = ViewModelProviders.of(addActionActivity, newActionViewModelFactory)
        .get(NewActionViewModel::class.java)

    @Provides
    @AddActionActivityScope
    fun provideActionViewModelFactory(
        addActionUseCase: AddActionUseCase
    ) =
        NewActionViewModelFactory(
            addActionUseCase,
            addActionActivity.application
        )

    @Provides
    @AddActionActivityScope
    fun provideAddActionUseCase(actionsRepository: ActionsRepository): AddActionUseCase {
        return AddActionUseCase(actionsRepository)
    }
}