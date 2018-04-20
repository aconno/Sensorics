package com.aconno.acnsensa.dagger.updateaction

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.ui.UpdateActionActivity
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel
import com.aconno.acnsensa.viewmodel.factory.ExistingActionViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class UpdateActionModule(private val updateActionActivity: UpdateActionActivity) {
    @Provides
    @UpdateActionActivityScope
    fun provideUpdateActionUseCase(actionsRepository: ActionsRepository) =
        UpdateActionUseCase(actionsRepository)

    @Provides
    @UpdateActionActivityScope
    fun provideGetActionUseCase(actionsRepository: ActionsRepository) =
        GetActionByIdUseCase(actionsRepository)

    @Provides
    @UpdateActionActivityScope
    fun provideDeleteActionUseCase(actionsRepository: ActionsRepository) =
        DeleteActionUseCase(actionsRepository)

    @Provides
    @UpdateActionActivityScope
    fun provideExistingActionViewModelFactory(
        updateActionUseCase: UpdateActionUseCase,
        getActionByIdUseCase: GetActionByIdUseCase,
        deleteActionUseCase: DeleteActionUseCase,
        notificationDisplay: NotificationDisplay,
        vibrator: Vibrator,
        smsSender: SmsSender
    ) = ExistingActionViewModelFactory(
        updateActionUseCase,
        getActionByIdUseCase,
        deleteActionUseCase,
        notificationDisplay,
        vibrator,
        smsSender,
        updateActionActivity.application
    )

    @Provides
    @UpdateActionActivityScope
    fun provideExistingValueViewModel(
        existingActionViewModelFactory: ExistingActionViewModelFactory
    ) =
        ViewModelProviders.of(updateActionActivity, existingActionViewModelFactory)
            .get(ExistingActionViewModel::class.java)
}