package com.aconno.sensorics.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.logs.AddLogUseCase
import com.aconno.sensorics.domain.interactor.logs.DeleteDeviceLogsUseCase
import com.aconno.sensorics.domain.interactor.logs.GetDeviceLogsUseCase
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LoggingLevel
import com.aconno.sensorics.model.LogModel
import com.aconno.sensorics.model.mapper.LogModelMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * @author julio.mendoza on 3/4/19.
 */
class LoggingViewModel(
        private val getDeviceLogsUseCase: GetDeviceLogsUseCase,
        private val deleteDeviceLogsUseCase: DeleteDeviceLogsUseCase,
        private val addLogUseCase: AddLogUseCase,
        private val logModelMapper: LogModelMapper
) : ViewModel() {

    private val logList = arrayListOf<LogModel>()
    private val logItemsLiveData = MutableLiveData<ArrayList<LogModel>>()
    private val disposables = CompositeDisposable()

    fun getLogItemsLiveData(): LiveData<ArrayList<LogModel>> = logItemsLiveData

    fun getDeviceLogs(deviceMacAddress: String) {
        val disposable = getDeviceLogsUseCase.execute(deviceMacAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { logs, throwable ->
                    logs?.let { list ->
                        logList.addAll(list.map { logModelMapper.transform(it) })
                        postValue()
                    }

                    throwable?.let {
                        Timber.e(it)
                    }
                }
        disposables.add(disposable)
    }

    fun deleteDeviceLogs(deviceMacAddress: String) {
        val disposable = deleteDeviceLogsUseCase.execute(deviceMacAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    logList.clear()
                    postValue()
                }

        disposables.add(disposable)
    }

    fun logInfo(info: String, deviceMacAddress: String) {
        log(info, deviceMacAddress, LoggingLevel.INFO)
    }

    fun logError(info: String, deviceMacAddress: String) {
        log(info, deviceMacAddress, LoggingLevel.ERROR)
    }

    fun logWarning(info: String, deviceMacAddress: String) {
        log(info, deviceMacAddress, LoggingLevel.WARNING)
    }

    @SuppressLint("CheckResult")
    private fun log(info: String, deviceMacAddress: String, loggingLevel: LoggingLevel) {
        val log = Log(info, System.currentTimeMillis(), loggingLevel, deviceMacAddress)
        val disposable = addLogUseCase.execute(log)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    logList.add(logModelMapper.transform(log))
                    postValue()
                }

        disposables.add(disposable)
    }

    private fun postValue() {
        logItemsLiveData.postValue(logList)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}