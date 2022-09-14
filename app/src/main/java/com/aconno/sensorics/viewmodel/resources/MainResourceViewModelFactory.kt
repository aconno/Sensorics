package com.aconno.sensorics.viewmodel.resources

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory

class MainResourceViewModelFactory(
    private val getMainResourceUseCase: GetMainResourceUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MainResourceViewModel(getMainResourceUseCase)
        return getViewModel(viewModel, modelClass)
    }
}