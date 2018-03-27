package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase
import com.aconno.acnsensa.viewmodel.ActionViewModel

/**
 * @author aconno
 */
class ActionViewModelFactory(
    private val addActionUseCase: AddActionUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionViewModel(addActionUseCase)
        return getViewModel(viewModel, modelClass)
    }
}