package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * @author aconno
 */
abstract class BaseViewModelFactory : ViewModelProvider.Factory {

    protected fun <T : ViewModel?> getViewModel(viewModel: ViewModel, modelClass: Class<T>): T {
        val result = listOf(viewModel).filterIsInstance(modelClass)
        if (result.size == 1) {
            return result[0]
        } else {
            throw IllegalArgumentException("Failed to cast. Invalid view model factory")
        }
    }
}