package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ResourcesInitializer
import com.aconno.sensorics.viewmodel.SplashViewModel

class SplashViewModelFactory(val resourcesInitializer: ResourcesInitializer) :
    BaseViewModelFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = SplashViewModel(resourcesInitializer)

        return getViewModel(viewModel, modelClass)
    }
}