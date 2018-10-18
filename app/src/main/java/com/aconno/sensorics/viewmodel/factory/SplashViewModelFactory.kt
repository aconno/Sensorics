package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.repository.RemoteFormatRepository
import com.aconno.sensorics.viewmodel.SplashViewModel

class SplashViewModelFactory(
    private val remoteFormatRepository: RemoteFormatRepository
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SplashViewModel(remoteFormatRepository)

        return getViewModel(viewModel, modelClass)
    }
}