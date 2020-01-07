package com.aconno.sensorics.domain.actions

import com.aconno.sensorics.domain.ifttt.Condition
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.model.Device

class GeneralAction(
    override var id: Long,
    override val name: String,
    override val device: Device,
    override val condition: Condition,
    override val outcome: Outcome,
    override val active: Boolean,
    override val timeFrom: Int,
    override val timeTo: Int
) : Action