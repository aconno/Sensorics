package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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