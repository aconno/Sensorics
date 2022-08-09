package com.aconno.sensorics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import timber.log.Timber

class DashboardViewModel(
    private val readingsStream: Flowable<List<Reading>>
) : ViewModel() {

    private val mutableUrl = MutableLiveData<String>()
    private var disposable: Disposable? = null

    val url: LiveData<String> = mutableUrl

    fun initViewModel() {
        mutableUrl.postValue("https://aconno.de/sensorics/dashboard/dashboard.html?timestamp=${System.currentTimeMillis()}")
    }

    fun subscribe() {
        disposable = readingsStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { processSensorValues(it) }
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