package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer
import com.aconno.acnsensa.viewmodel.NewActionViewModel

/**
 * @author aconno
 */
class NewActionViewModelFactory(
    private val addActionUseCase: AddActionUseCase,
    private val notificationDisplay: NotificationDisplay,
    private val vibrator: Vibrator,
    private val smsSender: SmsSender,
    private val textToSpeechPlayer: TextToSpeechPlayer,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            NewActionViewModel(
                addActionUseCase,
                notificationDisplay,
                vibrator,
                smsSender,
                textToSpeechPlayer,
                application
            )
        return getViewModel(viewModel, modelClass)
    }
}