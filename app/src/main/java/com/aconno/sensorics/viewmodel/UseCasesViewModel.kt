package com.aconno.sensorics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.resources.GetUseCaseResourceUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import timber.log.Timber

class UseCasesViewModel(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase,
    private val getUseCaseResourceUseCase: GetUseCaseResourceUseCase
) : ViewModel() {

    private val mutableUrl = MutableLiveData<String>()
    val url: LiveData<String> = mutableUrl
    val urlError = SingleLiveEvent<Unit>()

    val mutableHideProgress = SingleLiveEvent<Unit>()
    val mutableShowProgress = SingleLiveEvent<Unit>()

    private var macAddress: String? = null
    private var name: String? = null
    private var disposable: Disposable? = null
    private var htmlDisposable: Disposable? = null

    fun initViewModel(macAddress: String, name: String) {
        this.macAddress = macAddress
        this.name = name

        mutableShowProgress.postValue(Unit)
        htmlDisposable = getUseCaseResourceUseCase.execute(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::success, ::error)
    }

    private fun success(localUrl: String?) {
        Timber.d(localUrl)
        mutableHideProgress.postValue(Unit)
        localUrl?.let {
            mutableUrl.postValue(it)
        }
    }

    private fun error(error: Throwable?) {
        urlError.postValue(Unit)
        mutableHideProgress.postValue(Unit)
    }

    fun subscribe() {
        macAddress?.let { macAddress ->
            disposable = readingsStream
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap { filterByMacUseCase.execute(it, macAddress).toFlowable() }
                .subscribe { processSensorValues(it) }
        }
    }

    fun unsubscribe() {
        disposable?.dispose()
        htmlDisposable?.dispose()
    }

    private fun processSensorValues(values: List<Reading>) {
        val output = values.associateBy({ it.name }, { it.value })
        displaySensorValues(output)
    }

    private fun displaySensorValues(values: Map<String, Number>) {
        callJavaScript("onData", JSONObject(values).toString())

    }

    private fun callJavaScript(methodName: String, jsonParams: String) {
        val generator = JavascriptCallGenerator()
        val call = generator.generateCall(methodName, jsonParams)
        mutableUrl.postValue(call)
    }
}