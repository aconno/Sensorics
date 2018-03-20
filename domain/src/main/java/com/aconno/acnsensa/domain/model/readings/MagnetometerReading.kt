package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class MagnetometerReading(
    timestamp: Long,
    val magnetometerX: Number,
    val magnetometerY: Number,
    val magnetometerZ: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.MAGNETOMETER
    }

    override fun getCsvString(): String {
        return timestamp.toString() + "," +
                magnetometerX.toString() + "," +
                magnetometerY.toString() + "," +
                magnetometerZ.toString()
    }
}