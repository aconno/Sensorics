package com.aconno.acnsensa.device.bluetooth

import com.aconno.acnsensa.domain.scanning.BluetoothState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BluetoothStateListener {

    private val bluetoothStates: PublishSubject<BluetoothState> = PublishSubject.create()

    fun getBluetoothStates(): Observable<BluetoothState> {
        return bluetoothStates
    }

    fun onBluetoothStateEvent(newState: BluetoothState) {
        bluetoothStates.onNext(newState)
    }
}