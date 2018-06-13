package com.aconno.acnsensa.domain.interactor.filter

import com.aconno.acnsensa.domain.model.Device

data class Reading(
    val timestamp: Long,
    val device: Device,
    val value: Number,
    val type: ReadingType
) {

    fun toCsvString(): String {
        return "$timestamp, ${device.macAddress}, ${type.toString()}, $value"
    }
}