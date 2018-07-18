package com.aconno.sensorics.domain.actions

import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome

interface Action {
    val id: Long
    val name: String
    val deviceMacAddress: String
    val condition: Condition
    val outcome: Outcome
}