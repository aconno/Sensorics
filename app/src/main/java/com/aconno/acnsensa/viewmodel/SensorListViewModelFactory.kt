package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable

/**
 * @author aconno
 */
class SensorListViewModelFactory(
    private val sensorValues: Flowable<Map<String, Number>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") //Safe to suppress since as? casting is being used.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: T? =
            SensorListViewModel(
                sensorValues
            ) as? T
        viewModel?.let { return viewModel }

        throw IllegalArgumentException("Illegal cast for SensorListViewModel")
    }
}