package com.aconno.sensorics.viewmodel.resources

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.resources.GetConnectionResourceUseCase
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory

class MainResourceViewModelFactory(
    private val getMainResourceUseCase: GetMainResourceUseCase,
    private val getConnectionResourceUseCase: GetConnectionResourceUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = MainResourceViewModel(getMainResourceUseCase, getConnectionResourceUseCase)
        return getViewModel(viewModel, modelClass)
    }
}