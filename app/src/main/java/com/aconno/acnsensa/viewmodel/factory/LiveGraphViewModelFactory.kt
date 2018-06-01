package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import io.reactivex.Flowable

class LiveGraphViewModelFactory(
    private val sensorReadings: Flowable<List<SensorReading>>,
    private val getReadingsUseCase: GetReadingsUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = LiveGraphViewModel(sensorReadings, getReadingsUseCase, application)
        return getViewModel(viewModel, modelClass)
    }
}