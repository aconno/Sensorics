package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.ifttt.outcome.Outcome

/**
 * @author aconno
 */
class GeneralAction(
    override val id: Long,
    override val name: String,
    override val condition: Condition,
    override val outcome: Outcome
) : Action