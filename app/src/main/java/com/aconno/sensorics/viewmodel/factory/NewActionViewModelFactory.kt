package com.aconno.sensorics.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.viewmodel.NewActionViewModel

class NewActionViewModelFactory(
    private val addActionUseCase: AddActionUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            NewActionViewModel(
                addActionUseCase,
                application
            )
        return getViewModel(viewModel, modelClass)
    }
}