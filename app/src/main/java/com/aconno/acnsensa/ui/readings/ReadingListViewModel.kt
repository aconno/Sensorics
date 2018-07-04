package com.aconno.acnsensa.ui.readings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.model.Reading
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ReadingListViewModel(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : ViewModel() {

    private var disposable: Disposable? = null

    private val readingsLiveData = MutableLiveData<List<Reading>>()

    fun getReadingsLiveData(): LiveData<List<Reading>> {
        return readingsLiveData
    }

    fun init(macAddress: String) {
        disposable?.dispose()
        disposable = readingsStream
            .observeOn(AndroidSchedulers.mainThread())
            .concatMap { filterByMacUseCase.execute(it, macAddress).toFlowable() }
            .subscribe { updateLiveData(it) }
    }

    private fun updateLiveData(readings: List<Reading>) {
        readingsLiveData.value = readings
    }
}