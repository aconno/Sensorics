package com.aconno.sensorics.ui.readings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
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