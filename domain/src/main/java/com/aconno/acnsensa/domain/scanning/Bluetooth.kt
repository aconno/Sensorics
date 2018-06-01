package com.aconno.acnsensa.domain.scanning

import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable

interface Bluetooth {

    fun enable()

    fun disable()

    fun startScanning()

    fun stopScanning()

    fun getScanResults(): Flowable<ScanResult>

    fun getScanEvents(): Flowable<ScanEvent>

    fun getStateEvents(): Flowable<BluetoothState>
}