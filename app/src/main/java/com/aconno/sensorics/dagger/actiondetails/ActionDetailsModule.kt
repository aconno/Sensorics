package com.aconno.sensorics.dagger.actiondetails

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.actions.ActionDetailsViewModel
import com.aconno.sensorics.ui.actions.ActionDetailsViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class ActionDetailsModule {

    @Provides
    @ActionDetailsActivityScope
    fun provideActionDetailsViewModelFactory(
        savedDevicesStream: Flowable<List<Device>>,
        formatMatcher: FormatMatcher,
        getActionByIdUseCase: GetActionByIdUseCase,
        addActionUseCase: AddActionUseCase,
        getIconUseCase: GetIconUseCase
    ) = ActionDetailsViewModelFactory(
        savedDevicesStream,
        formatMatcher,
        getActionByIdUseCase,
        addActionUseCase,
        getIconUseCase
    )

    @Provides
    @ActionDetailsActivityScope
    fun provideActionDetailsViewModel(
        actionDetailsActivity: ActionDetailsActivity,
        actionDetailsViewModelFactory: ActionDetailsViewModelFactory
    ) = ViewModelProviders.of(actionDetailsActivity, actionDetailsViewModelFactory)
        .get(ActionDetailsViewModel::class.java)

}