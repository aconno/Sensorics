package com.aconno.acnsensa.domain.model

data class Reading(
    val timestamp: Long,
    val device: Device,
    val value: Number,
    val type: String
) {

    fun toCsvString(): String {
        return "$timestamp, ${device.macAddress}, $type, $value"
    }
}