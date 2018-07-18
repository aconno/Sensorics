package com.aconno.sensorics.domain.actions

import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import com.aconno.sensorics.domain.model.Device

class GeneralAction(
    override val id: Long,
    override val name: String,
    override val device: Device,
    override val condition: Condition,
    override val outcome: Outcome
) : Action