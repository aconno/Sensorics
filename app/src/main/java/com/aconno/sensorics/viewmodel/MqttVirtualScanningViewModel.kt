package com.aconno.sensorics.viewmodel

import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.MqttVirtualScanningService
import com.aconno.sensorics.SensoricsApplication
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MqttVirtualScanningViewModel(
    application: SensoricsApplication
) : AndroidViewModel(application) {

    private val disposables = CompositeDisposable()


    fun startScanning(serverURI: String, clientId: String) {
        Timber.d("startScanning")

        MqttVirtualScanningService.start(getApplication(), serverURI, clientId)
    }

    fun stopScanning() {
        Timber.d("stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent(MqttVirtualScanningService.STOP))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}