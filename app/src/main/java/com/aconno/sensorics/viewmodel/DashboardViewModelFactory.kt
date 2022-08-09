package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory
import io.reactivex.Flowable

class DashboardViewModelFactory(
    private val readingsStream: Flowable<List<Reading>>
) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DashboardViewModel(
            readingsStream
        )
        return getViewModel(viewModel, modelClass)
    }
}