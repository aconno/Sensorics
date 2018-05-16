package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import io.reactivex.Flowable

/**
 * @author aconno
 */
class LiveGraphViewModelFactory(
    private val sensorValues: Flowable<Map<String, Number>>,
    private val getReadingsUseCase: GetReadingsUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = LiveGraphViewModel(sensorValues, getReadingsUseCase, application)
        return getViewModel(viewModel, modelClass)
    }
}