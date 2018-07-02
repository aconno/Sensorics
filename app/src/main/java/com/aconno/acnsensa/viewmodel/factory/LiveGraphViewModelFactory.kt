package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetReadingsUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import io.reactivex.Flowable

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