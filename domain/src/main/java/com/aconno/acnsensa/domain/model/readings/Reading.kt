package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

data class Reading(
    val values: List<Number>,
    val timestamp: Long,
    val sensorType: SensorType
) {
    fun getCsvString(): String {
        val stringValues = values.joinToString(separator = ", ")
        return "$timestamp, $stringValues"
    }
}