package com.aconno.sensorics.ui

interface BleScanner {
    fun startScan(filterByDevice: Boolean = true)
    fun stopScan()
}