package com.aconno.sensorics.domain.actions

import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import com.aconno.sensorics.domain.model.Device

interface Action {
    val id: Long
    val name: String
    val device: Device
    val condition: Condition
    val outcome: Outcome
}