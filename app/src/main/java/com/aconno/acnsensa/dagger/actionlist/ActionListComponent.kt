package com.aconno.acnsensa.dagger.actionlist

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.ui.ActionListFragment
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [ActionListModule::class])
@ActionListScope
interface ActionListComponent {
    //Exposed dependencies for child components.
    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun actionOptionsViewModel(): ActionOptionsViewModel

    //Classes which can accept injected dependencies.
    fun inject(actionsListFragment: ActionListFragment)
}

