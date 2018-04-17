package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
interface Action {
    val id: Long
    val name: String
    val condition: Condition
    val outcome: Outcome
    fun processInput(input: Input)
}