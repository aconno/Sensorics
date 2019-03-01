package com.aconno.sensorics.model.javascript

//Mirror Object of Slot.js
data class SlotJS(
    val frameType: Int,
    val frame: MutableMap<String, Any>,
    val advertisingInterval: Long,
    val rssi1m: Int,
    val radioTx: Int,
    val triggerEnabled: Boolean,
    val triggerType: Int
)