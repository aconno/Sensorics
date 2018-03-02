package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class TemperatureReading(
    timestamp: Long,
    val temperature: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.TEMPERATURE
    }
}