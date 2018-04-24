package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.ifttt.outcome.Outcome

/**
 * @author aconno
 */
interface Action {
    val id: Long
    val name: String
    val condition: Condition
    val outcome: Outcome
}