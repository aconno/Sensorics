package com.aconno.sensorics.dagger.beacon_settings

interface BeaconSettingsFragmentListener {
    fun updateFirmware()
    fun resetFactory()
    fun powerOff()
    fun addPassword()
}