package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.Outcome

class GeneralAction(
    override val id: Long,
    override val name: String,
    override val deviceMacAddress : String,
    override val condition: Condition,
    override val outcome: Outcome
) : Action