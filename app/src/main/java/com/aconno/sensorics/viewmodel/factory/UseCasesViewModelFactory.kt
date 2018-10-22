package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.viewmodel.UseCasesViewModel
import io.reactivex.Flowable

class UseCasesViewModelFactory(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = UseCasesViewModel(
            readingsStream,
            filterByMacUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}