package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(
    private val advertisementFormatRepository: AdvertisementFormatRepository
) : ViewModel() {

    private var updateFormatsDisposable: Disposable? = null

    private val updateCompleteEvent = SingleLiveEvent<Unit>()

    fun getUpdateCompleteEvent(): LiveData<Unit> = updateCompleteEvent

    private val updateErrorEvent = SingleLiveEvent<String>()

    fun getUpdateErrorEvent(): LiveData<String> = updateErrorEvent

    fun updateAdvertisementFormats() {
        updateFormatsDisposable = advertisementFormatRepository.updateAdvertisementFormats()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { onFormatsUpdateComplete() },
                { onFormatsUpdateError(it) }
            )
    }

    private fun onFormatsUpdateComplete() {
        updateCompleteEvent.postValue(Unit)
    }

    private fun onFormatsUpdateError(throwable: Throwable) {
        updateErrorEvent.postValue(throwable.message)
    }

    override fun onCleared() {
        super.onCleared()
        updateFormatsDisposable?.dispose()
    }
}