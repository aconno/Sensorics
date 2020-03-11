package com.aconno.sensorics.dagger.beacon_settings

interface BeaconGeneralFragmentListener {
    fun updateFirmware()
    fun resetFactory()
    fun powerOff()
    fun addPassword()
}