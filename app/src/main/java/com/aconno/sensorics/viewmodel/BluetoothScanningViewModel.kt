package com.aconno.sensorics.viewmodel

import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.service.BluetoothScanningService
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.ScanEvent
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class BluetoothScanningViewModel(
    application: SensoricsApplication,
    private val bluetooth: Bluetooth
) : AndroidViewModel(application) {

    private val scanEvent = MutableLiveData<ScanEvent>()

    fun getScanEvent(): LiveData<ScanEvent> = scanEvent

    private val disposables = CompositeDisposable()

    init {
        subscribeToScanEvents()
    }

    private fun subscribeToScanEvents() {
        disposables.add(
            bluetooth.getScanEvent()
                .subscribe { scanEvent.postValue(it) }
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}