package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.repository.RemoteFormatRepository
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(
    private val remoteFormatRepository: RemoteFormatRepository
) : ViewModel() {

    private var updateFormatsDisposable: Disposable? = null

    private val mutableUpdateCompleteEvent = SingleLiveEvent<Unit>()

    val updateCompleteEvent: LiveData<Unit> = mutableUpdateCompleteEvent

    private val mutableUpdateErrorEvent = SingleLiveEvent<Throwable>()

    val updateErrorEvent: LiveData<Throwable> = mutableUpdateErrorEvent

    fun updateAdvertisementFormats() {
        if (updateFormatsDisposable == null) {
            updateFormatsDisposable = remoteFormatRepository.updateAdvertisementFormats()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { onFormatsUpdateComplete() },
                    { onFormatsUpdateError(it) }
                )
        }
    }

    private fun onFormatsUpdateComplete() {
        mutableUpdateCompleteEvent.postValue(Unit)
    }

    private fun onFormatsUpdateError(throwable: Throwable) {
        mutableUpdateErrorEvent.postValue(throwable)
    }

    override fun onCleared() {
        super.onCleared()
        updateFormatsDisposable?.dispose()
    }
}
