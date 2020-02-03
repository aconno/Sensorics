package com.aconno.sensorics.dagger.settings_framework

interface BeaconGeneralFragmentListener {
    fun updateFirmware()
    fun resetFactory()
    fun powerOff()
    fun addPassword()
}