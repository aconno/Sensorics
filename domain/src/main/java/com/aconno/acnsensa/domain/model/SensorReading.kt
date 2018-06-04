package com.aconno.acnsensa.domain.model

data class SensorReading(
    val timestamp: Long,
    val device: Device,
    val value: Number,
    val sensorType: SensorTypeSingle
)