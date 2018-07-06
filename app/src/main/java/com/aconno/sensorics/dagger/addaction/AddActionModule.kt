package com.aconno.sensorics.dagger.addaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.ui.actions.AddActionActivity
import com.aconno.sensorics.viewmodel.NewActionViewModel
import com.aconno.sensorics.viewmodel.factory.NewActionViewModelFactory
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