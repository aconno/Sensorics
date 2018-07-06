package com.aconno.sensorics.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.UpdateActionUseCase
import com.aconno.sensorics.viewmodel.ActionViewModel

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