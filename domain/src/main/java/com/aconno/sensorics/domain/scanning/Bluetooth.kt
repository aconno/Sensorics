package com.aconno.sensorics.domain.scanning

import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.model.ScanEvent
import io.reactivex.Flowable
import com.aconno.sensorics.domain.model.Device

interface Bluetooth {

    fun enable()

    fun disable()

    fun startScanning()

    fun startScanning(devices: List<Device>)

    fun stopScanning()

    fun getScanResults(): Flowable<ScanResult>

    fun getScanEvents(): Flowable<ScanEvent>

    fun getStateEvents(): Flowable<BluetoothState>
}