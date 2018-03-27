package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aconno.acnsensa.domain.ifttt.AddActionUseCase

/**
 * @author aconno
 */
class ActionViewModelFactory(
    private val addActionUseCase: AddActionUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionViewModel(addActionUseCase)
        val result = listOf(viewModel).filterIsInstance(modelClass)
        if (result.size == 1) {
            return result[0]
        } else {
            throw IllegalArgumentException()
        }
    }
}