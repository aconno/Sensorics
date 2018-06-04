package com.aconno.acnsensa.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.SingleLiveEvent
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.domain.scanning.BluetoothState
import io.reactivex.disposables.Disposable

class BluetoothViewModel(
    private val bluetooth: Bluetooth,
    private val bluetoothStateReceiver: BluetoothStateReceiver,
    private val application: Application
) : ViewModel() {

    val bluetoothState: MutableLiveData<BluetoothState> = SingleLiveEvent()

    private var bluetoothStatesSubscription: Disposable? = null

    fun enableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.enable()
    }

    fun observeBluetoothState() {
        application.applicationContext.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        val bluetoothStates = bluetooth.getStateEvents()
        bluetoothStatesSubscription = bluetoothStates.subscribe { bluetoothState.value = it }
    }

    fun stopObservingBluetoothState() {
        application.applicationContext.unregisterReceiver(bluetoothStateReceiver)
        bluetoothStatesSubscription?.dispose()
    }
}