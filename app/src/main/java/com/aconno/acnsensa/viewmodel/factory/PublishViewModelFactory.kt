package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.viewmodel.PublishViewModel

class PublishViewModelFactory(
    private val application: Application,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishViewModel(
            application,
            addGooglePublishUseCase,
            updateGooglePublishUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}