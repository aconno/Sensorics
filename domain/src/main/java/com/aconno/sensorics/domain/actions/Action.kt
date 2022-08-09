package com.aconno.sensorics.domain.actions

import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.model.Device

interface Action {
    var id: Long
    val name: String
    val device: Device
    val condition: Condition
    val outcome: Outcome
    var active: Boolean
    val timeFrom: Int
    val timeTo: Int
}