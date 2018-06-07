package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateActionUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.viewmodel.ActionViewModel

class ActionViewModelFactory(
    private val application: Application,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val updateActionUseCase: UpdateActionUseCase,
    private val deleteActionUseCase: DeleteActionUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionViewModel(
            application,
            getActionByIdUseCase,
            updateActionUseCase,
            deleteActionUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}