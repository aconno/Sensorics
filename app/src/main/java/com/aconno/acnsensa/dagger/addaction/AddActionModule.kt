package com.aconno.acnsensa.dagger.addaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.ui.AddActionActivity
import com.aconno.acnsensa.viewmodel.NewActionViewModel
import com.aconno.acnsensa.viewmodel.factory.NewActionViewModelFactory
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
        newActionViewModelFactory: NewActionViewModelFactory
    ) = ViewModelProviders.of(addActionActivity, newActionViewModelFactory)
        .get(NewActionViewModel::class.java)

    @Provides
    @AddActionActivityScope
    fun provideActionViewModelFactory(
        addActionUseCase: AddActionUseCase,
        notificationDisplay: NotificationDisplay,
        vibrator: Vibrator,
        smsSender: SmsSender
    ) =
        NewActionViewModelFactory(
            addActionUseCase,
            notificationDisplay,
            vibrator,
            smsSender,
            addActionActivity.application
        )

    @Provides
    @AddActionActivityScope
    fun provideAddActionUseCase(actionsRepository: ActionsRepository): AddActionUseCase {
        return AddActionUseCase(actionsRepository)
    }
}