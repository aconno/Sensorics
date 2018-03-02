package com.aconno.acnsensa.domain.model.readings

import com.aconno.acnsensa.domain.model.SensorType

abstract class Reading(
    val timestamp: Long
) {
    abstract fun getSensorType(): SensorType
}