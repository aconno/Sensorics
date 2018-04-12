package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.domain.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel

/**
 * @author aconno
 */
class ExistingActionViewModelFactory(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val notificationDisplay: NotificationDisplay,
    private val vibrator: Vibrator,
    private val smsSender: SmsSender
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            ExistingActionViewModel(
                updateActionUseCase,
                getActionByIdUseCase,
                deleteActionUseCase,
                notificationDisplay,
                vibrator,
                smsSender
            )
        return getViewModel(viewModel, modelClass)
    }
}