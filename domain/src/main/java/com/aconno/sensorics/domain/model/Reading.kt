package com.aconno.sensorics.domain.model

data class Reading(
    val timestamp: Long,
    val device: Device,
    val value: Number,
    val name: String,
    val rssi: Int,
    val advertisementId: String
) {
    fun toCsvString(): String {
        return "$timestamp, ${device.macAddress}, $rssi, $name, $value"
    }
}