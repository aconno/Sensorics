package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class PressureReading(
    timestamp: Long,
    val pressure: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.PRESSURE
    }

    override fun getCsvString(): String {
        return timestamp.toString() + "," +
                pressure.toString()
    }
}