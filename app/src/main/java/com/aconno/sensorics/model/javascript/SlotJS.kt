package com.aconno.sensorics.model.javascript

import com.aconno.sensorics.device.beacon.Slot

//Mirror Object of Slot.js
data class SlotJS(
    val frameType: String,
    val frame: MutableMap<String, String>,
    val name: String,
    val active:Boolean,
    /**
     * true means [Slot.AdvertisingModeParameters.Mode.EVENT], false means [Slot.AdvertisingModeParameters.Mode.INTERVAL]
     */
    val advertisingMode: Boolean,
    val packetCount: Int,
    val supportedtxPower: List<Byte>,
    val txPower: Int,
    val readOnly: Boolean,
    val addInterval: Long
)