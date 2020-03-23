package com.aconno.sensorics.device.beacon.baseimpl

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slots

interface BeaconSettingsFactory {
    fun createArbitraryData() : ArbitraryData
    fun createParameters() : Parameters
    fun createSlots(bytes: ByteArray? = null) : Slots
}