package com.aconno.sensorics.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.GetReadingsUseCase
import com.aconno.sensorics.viewmodel.LiveGraphViewModel

class LiveGraphViewModelFactory(
    private val getReadingsUseCase: GetReadingsUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = LiveGraphViewModel(
            getReadingsUseCase,
            application
        )
        return getViewModel(viewModel, modelClass)
    }
}