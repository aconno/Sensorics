package com.aconno.sensorics.viewmodel

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aconno.sensorics.SingleLiveEvent
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothState
import io.reactivex.disposables.Disposable

class BluetoothViewModel(
    private val bluetooth: Bluetooth
) : ViewModel() {

    val bluetoothState: MutableLiveData<BluetoothState> = SingleLiveEvent()

    private var bluetoothStatesSubscription: Disposable? = null

    fun enableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.enable()
    }

    fun isBluetoothSupported() = BluetoothAdapter.getDefaultAdapter() != null

    fun observeBluetoothState() {
        val bluetoothStates = bluetooth.getStateEvents()
        bluetoothStatesSubscription = bluetoothStates.subscribe { bluetoothState.value = it }
    }

    fun stopObservingBluetoothState() {
        bluetoothStatesSubscription?.dispose()
    }
}