package com.aconno.acnsensa.ui.readings

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.viewmodel.factory.BaseViewModelFactory
import io.reactivex.Flowable

class ReadingListViewModelFactory(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ReadingListViewModel(
            readingsStream,
            filterByMacUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}