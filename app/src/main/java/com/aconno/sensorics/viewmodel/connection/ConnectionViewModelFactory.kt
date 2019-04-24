package com.aconno.sensorics.viewmodel.connection

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.resources.GetConnectionResourceUseCase
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory

class ConnectionViewModelFactory(
    private val getConnectionResourceUseCase: GetConnectionResourceUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ConnectionViewModel(getConnectionResourceUseCase)
        return getViewModel(viewModel, modelClass)
    }
}