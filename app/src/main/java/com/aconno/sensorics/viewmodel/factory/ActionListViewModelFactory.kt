package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.viewmodel.ActionListViewModel

class ActionListViewModelFactory(
    private val getAllActionsUseCase: GetAllActionsUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val addActionUseCase: AddActionUseCase,
    private val convertActionsToJsonUseCase: ConvertObjectsToJsonUseCase<Action>,
    private val convertJsonToActionsUseCase: ConvertJsonToActionsUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionListViewModel(
            getAllActionsUseCase,
            deleteActionUseCase,
            addActionUseCase,
            convertActionsToJsonUseCase,
            convertJsonToActionsUseCase
        )

        return getViewModel(viewModel, modelClass)
    }
}