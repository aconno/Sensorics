package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.domain.model.ScanEvent
import com.aconno.sensorics.domain.scanning.Bluetooth
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: SensoricsApplication
) : AndroidViewModel(application) {

    private val result: MutableLiveData<ScanEvent> = MutableLiveData()

    private val disposables = CompositeDisposable()

    init {
        subscribe()
    }

    private fun subscribe() {
        val observable: Flowable<ScanEvent> = bluetooth.getScanEvents()
        disposables.add(
            observable.subscribe { result.value = it }
        )
    }

    fun startScanning(filterByDevice: Boolean) {
        Timber.d("startScanning")

        BluetoothScanningService.start(getApplication(), filterByDevice)
    }

    fun stopScanning() {
        Timber.d("stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent("com.aconno.sensorics.STOP"))
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}