package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.AddActionUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.viewmodel.NewActionViewModel

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