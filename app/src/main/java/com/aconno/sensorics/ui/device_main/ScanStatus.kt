package com.aconno.sensorics.ui.device_main

interface ScanStatus {
    /**
     * true = Online
     * false = Offline
     * @param isOnline Boolean
     */
    fun setStatus(isOnline: Boolean, force: Boolean = false)
}