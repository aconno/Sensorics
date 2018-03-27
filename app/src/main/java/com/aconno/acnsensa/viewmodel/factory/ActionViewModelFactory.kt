package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.viewmodel.ActionViewModel

/**
 * @author aconno
 */
class ActionViewModelFactory(
    private val addActionUseCase: AddActionUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionViewModel(addActionUseCase, application)
        return getViewModel(viewModel, modelClass)
    }
}