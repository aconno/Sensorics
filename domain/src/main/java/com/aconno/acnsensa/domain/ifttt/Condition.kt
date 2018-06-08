package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.SensorTypeSingle

interface Condition {
    val sensorType: SensorTypeSingle
    val limit: Float
    val type: Int
    fun isSatisfied(input: Input): Boolean
}