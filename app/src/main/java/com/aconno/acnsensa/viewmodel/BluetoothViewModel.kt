package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.BluetoothState
import io.reactivex.disposables.Disposable

class BluetoothViewModel(
    private val bluetooth: Bluetooth,
    private val bluetoothStateReceiver: BluetoothStateReceiver,
    private val localBroadcastManager: LocalBroadcastManager
) : ViewModel() {

    val bluetoothState: MutableLiveData<BluetoothState> = MutableLiveData()

    private var bluetoothStatesSubscription: Disposable? = null

    fun enableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.enable()
    }

    fun observeBluetoothState() {
        localBroadcastManager.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        val bluetoothStates = bluetooth.getStateEvents()
        bluetoothStatesSubscription = bluetoothStates.subscribe { bluetoothState.value = it }
    }

    fun stopObservingBluetoothState() {
        localBroadcastManager.unregisterReceiver(bluetoothStateReceiver)
        bluetoothStatesSubscription?.dispose()
    }
}