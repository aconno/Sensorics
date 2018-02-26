package com.aconno.acnsensa.domain

import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable

interface Bluetooth {

    fun enable()

    fun disable()

    fun startScanning()

    fun stopScanning()

    fun getScanResults(): Flowable<ScanResult>
}