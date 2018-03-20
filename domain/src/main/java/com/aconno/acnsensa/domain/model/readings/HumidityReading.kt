package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class HumidityReading(
    timestamp: Long,
    val humidity: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.HUMIDITY
    }

    override fun getCsvString(): String {
        return timestamp.toString() + "," +
                humidity.toString()
    }
}