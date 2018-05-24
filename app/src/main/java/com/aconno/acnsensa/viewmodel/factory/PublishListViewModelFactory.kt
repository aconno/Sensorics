package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.UpdateGooglePublishUseCase
import com.aconno.acnsensa.viewmodel.PublishListViewModel

class PublishListViewModelFactory(
    private val application: Application,
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val updateGooglePublishUseCase: UpdateGooglePublishUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = PublishListViewModel(
            application,
            getAllGooglePublishUseCase,
            updateGooglePublishUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}