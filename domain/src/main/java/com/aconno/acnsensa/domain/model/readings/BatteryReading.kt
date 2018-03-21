package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

/**
 * @aconno
 */
class BatteryReading(
    timestamp: Long,
    val batteryLevel: Number
) : Reading(timestamp) {

    override fun getSensorType(): SensorType {
        return SensorType.BATTERY_LEVEL
    }

    override fun getCsvString(): String {
        return "$timestamp, $batteryLevel"
    }
}