package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class LightReading(
    timestamp: Long,
    val light: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.LIGHT
    }

    override fun getCsvString(): String {
        return timestamp.toString() + "," +
                light.toString()
    }
}