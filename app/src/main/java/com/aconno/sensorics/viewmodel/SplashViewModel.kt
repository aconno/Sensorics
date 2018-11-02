package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import com.aconno.sensorics.domain.repository.RemoteFormatRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(
    private val remoteFormatRepository: RemoteFormatRepository,
    private val localUseCaseRepository: LocalUseCaseRepository
) : ViewModel() {

    private var updateFormatsDisposable: Disposable? = null

    private val mutableUpdateCompleteEvent = SingleLiveEvent<Unit>()

    val updateCompleteEvent: LiveData<Unit> = mutableUpdateCompleteEvent

    private val mutableUpdateErrorEvent = SingleLiveEvent<Throwable>()

    val updateErrorEvent: LiveData<Throwable> = mutableUpdateErrorEvent

    fun updateAdvertisementFormats() {
        if (updateFormatsDisposable == null) {
            updateFormatsDisposable = remoteFormatRepository.updateAdvertisementFormats()
                .mergeWith(Completable.fromAction { localUseCaseRepository.moveUsecasesFromAssetsToCache() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
