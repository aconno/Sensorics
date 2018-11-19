package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.viewmodel.SplashViewModel

class SplashViewModelFactory(
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SplashViewModel()

        return getViewModel(viewModel, modelClass)
    }
}