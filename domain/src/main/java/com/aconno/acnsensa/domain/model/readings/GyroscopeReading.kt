package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

class GyroscopeReading(
    timestamp: Long,
    val gyroscopeX: Number,
    val gyroscopeY: Number,
    val gyroscopeZ: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.GYROSCOPE
    }
}