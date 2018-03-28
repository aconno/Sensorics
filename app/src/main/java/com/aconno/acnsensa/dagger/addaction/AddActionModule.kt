package com.aconno.acnsensa.dagger.addaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
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
    fun provideActionViewModelFactory(
        addActionUseCase: AddActionUseCase,
        notificationDisplay: NotificationDisplay
    ) =
        ActionViewModelFactory(addActionUseCase, notificationDisplay, addActionActivity.application)

    @Provides
    @AddActionActivityScope
    fun provideAddActionUseCase(actionsRepository: ActionsRepository): AddActionUseCase {
        return AddActionUseCase(actionsRepository)
    }


}