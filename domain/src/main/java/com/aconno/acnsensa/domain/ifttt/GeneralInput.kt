package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.SensorTypeSingle

class GeneralInput(
    override val macAddress: String,
    override val value: Float,
    override val type: SensorTypeSingle,
    override val timestamp: Long
) : Input