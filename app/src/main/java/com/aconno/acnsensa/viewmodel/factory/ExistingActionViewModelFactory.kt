package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel

/**
 * @author aconno
 */
class ExistingActionViewModelFactory(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            ExistingActionViewModel(updateActionUseCase, getActionByIdUseCase, deleteActionUseCase)
        return getViewModel(viewModel, modelClass)
    }
}