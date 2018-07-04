package com.aconno.acnsensa.domain.model

data class Reading(
    val timestamp: Long,
    val device: Device,
    val value: Number,
    val name: String
) {

    fun toCsvString(): String {
        return "$timestamp, ${device.macAddress}, $name, $value"
    }
}