package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class AccelerometerReading(
    timestamp: Long,
    val accelerometerX: Number,
    val accelerometerY: Number,
    val accelerometerZ: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.ACCELEROMETER
    }

    override fun getCsvString(): String {
        return timestamp.toString() + "," +
                accelerometerX.toString() + "," +
                accelerometerY.toString() + "," +
                accelerometerZ.toString()
    }
}