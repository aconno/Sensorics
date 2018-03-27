package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.viewmodel.SensorListViewModel
import io.reactivex.Flowable

/**
 * @author aconno
 */
class SensorListViewModelFactory(
    private val sensorValues: Flowable<Map<String, Number>>
) : BaseViewModelFactory() {

    @Suppress("UNCHECKED_CAST") //Safe to suppress since as? casting is being used.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SensorListViewModel(sensorValues)
        return getViewModel(viewModel, modelClass)
    }
}