package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
interface Action {
    val name: String
    val condition: Condition
    val outcome: Outcome
    fun processInput(input: Input)
}