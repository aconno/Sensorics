package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.model.ScanEvent
import io.reactivex.Flowable
import timber.log.Timber

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: SensoricsApplication
) : AndroidViewModel(application) {

    private val result: MutableLiveData<ScanEvent> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {
        val observable: Flowable<ScanEvent> = bluetooth.getScanEvents()
        observable.subscribe { result.value = it }
    }

    fun startScanning() {
        Timber.d("startScanning")
        BluetoothScanningService.start(getApplication())
    }

    fun stopScanning() {
        Timber.d("stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent("com.aconno.sensorics.STOP"))
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }
}