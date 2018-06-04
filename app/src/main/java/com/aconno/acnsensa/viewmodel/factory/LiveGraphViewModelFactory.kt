package com.aconno.acnsensa.viewmodel.factory

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorReadingsUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterReadingsByMacAddressUseCase
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.viewmodel.LiveGraphViewModel
import io.reactivex.Flowable

class LiveGraphViewModelFactory(
    private val sensorReadings: Flowable<List<SensorReading>>,
    private val filterReadingsByMacAddressUseCase: FilterReadingsByMacAddressUseCase,
    private val getSensorReadingsUseCase: GetSensorReadingsUseCase,
    private val application: Application
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel =
            LiveGraphViewModel(
                sensorReadings,
                filterReadingsByMacAddressUseCase,
                getSensorReadingsUseCase,
                application
            )
        return getViewModel(viewModel, modelClass)
    }
}