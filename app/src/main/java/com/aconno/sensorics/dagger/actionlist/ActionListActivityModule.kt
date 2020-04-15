package com.aconno.sensorics.dagger.actionlist

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.ui.ActionListActivity
import com.aconno.sensorics.viewmodel.ActionListViewModel
import com.aconno.sensorics.viewmodel.factory.ActionListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ActionListActivityModule {
    @Provides
    @ActionListActivityScope
    fun providePublishListViewModel(
        activity: ActionListActivity,
        publishViewModelFactory: ActionListViewModelFactory
    ) = ViewModelProvider(
        activity,
        publishViewModelFactory
    ).get(ActionListViewModel::class.java)

    @Provides
    @ActionListActivityScope
    fun providePublishListViewModelFactory(
        getAllActionsUseCase: GetAllActionsUseCase,
        deleteActionUseCase: DeleteActionUseCase,
        addActionUseCase: AddActionUseCase,
        convertActionsToJsonUseCase: ConvertObjectsToJsonUseCase<Action>,
        convertJsonToActionsUseCase: ConvertJsonToActionsUseCase
    ) = ActionListViewModelFactory(
        getAllActionsUseCase,
        deleteActionUseCase,
        addActionUseCase,
        convertActionsToJsonUseCase,
        convertJsonToActionsUseCase
    )
}