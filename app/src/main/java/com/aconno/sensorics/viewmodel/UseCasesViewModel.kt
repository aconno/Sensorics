package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import timber.log.Timber

class UseCasesViewModel(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : ViewModel() {

    private val mutableUrl = MutableLiveData<String>()
    val url: LiveData<String> = mutableUrl

    private var macAddress: String? = null
    private var name: String? = null
    private var disposable: Disposable? = null

    fun initViewModel(macAddress: String, name: String) {
        this.macAddress = macAddress
        this.name = name

        mutableUrl.postValue("http://aconno.de/sensorics/${name.toLowerCase()}.html?ts=${System.currentTimeMillis()}")
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
        Timber.i("callJavaScript: call=$call")
        mutableUrl.postValue(call)
    }
}