package com.aconno.sensorics.ui

interface BleScanner {
    fun startScan(filterByDevice: Boolean = true): Boolean
    fun stopScan()
}