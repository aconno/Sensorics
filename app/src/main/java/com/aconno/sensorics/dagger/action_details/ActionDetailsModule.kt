package com.aconno.sensorics.dagger.action_details

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.aconno.sensorics.ui.actions.ActionDetailsViewModel
import com.aconno.sensorics.ui.actions.ActionDetailsViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class ActionDetailsModule(private val actionDetailsActivity: ActionDetailsActivity) {

    @Provides
    @ActionDetailsActivityScope
    fun provideActionDetailsViewModelFactory(
        savedDevicesStream: Flowable<List<Device>>,
        formatMatcher: FormatMatcher
    ) = ActionDetailsViewModelFactory(savedDevicesStream, formatMatcher)

    @Provides
    @ActionDetailsActivityScope
    fun provideActionDetailsViewModel(
        actionDetailsViewModelFactory: ActionDetailsViewModelFactory
    ) = ViewModelProviders.of(actionDetailsActivity, actionDetailsViewModelFactory)
        .get(ActionDetailsViewModel::class.java)
}