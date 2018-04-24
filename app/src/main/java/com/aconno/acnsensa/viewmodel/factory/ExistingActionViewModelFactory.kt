package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.viewmodel.ExistingActionViewModel

/**
 * @author aconno
 */
class ExistingActionViewModelFactory(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            ExistingActionViewModel(
                updateActionUseCase,
                getActionByIdUseCase,
                deleteActionUseCase,
                application
            )
        return getViewModel(viewModel, modelClass)
    }
}