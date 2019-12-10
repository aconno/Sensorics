package com.aconno.sensorics.model.javascript

//Mirror Object of Slot.js
data class SlotJS(
    val frameType: String,
    val frame: MutableMap<String, String>,
    val name: String
)