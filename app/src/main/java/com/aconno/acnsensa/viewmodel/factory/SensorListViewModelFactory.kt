package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.viewmodel.SensorListViewModel
import io.reactivex.Flowable

class SensorListViewModelFactory(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = SensorListViewModel(
            readingsStream,
            filterByMacUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}