package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.SensorTypeSingle

interface Input {
    val macAddress: String
    val value: Float
    val type: SensorTypeSingle
    val timestamp: Long
}