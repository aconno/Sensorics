package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.viewmodel.ActionOptionsViewModel

/**
 * @author aconno
 */
class ActionOptionsViewModelFactory(
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionOptionsViewModel(application)
        return getViewModel(viewModel, modelClass)
    }
}