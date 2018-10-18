package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import com.aconno.sensorics.viewmodel.SplashViewModel

class SplashViewModelFactory(
    private val advertisementFormatRepository: AdvertisementFormatRepository
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SplashViewModel(advertisementFormatRepository)

        return getViewModel(viewModel, modelClass)
    }
}