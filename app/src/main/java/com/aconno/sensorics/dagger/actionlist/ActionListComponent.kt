package com.aconno.sensorics.dagger.actionlist

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.domain.SmsSender
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.ifttt.NotificationDisplay
import com.aconno.sensorics.domain.ifttt.TextToSpeechPlayer
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.ui.ActionListFragment
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

    fun textToSpeechPlayer(): TextToSpeechPlayer

    fun getSavedDevicesUseCase(): GetSavedDevicesUseCase

    //Classes which can accept injected dependencies.
    fun inject(actionsListFragment: ActionListFragment)
}

