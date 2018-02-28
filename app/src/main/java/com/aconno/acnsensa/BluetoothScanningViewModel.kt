package com.aconno.acnsensa

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.model.ScanEvent
import io.reactivex.Flowable

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: Application
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
        Log.e("ACNSENSA", "startScanning")


        BluetoothScanningService.start(getApplication())
    }

    fun stopScanning() {
        Log.e("ACNSENSA", "stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent("com.aconno.acnsensa.STOP"))
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }
}



