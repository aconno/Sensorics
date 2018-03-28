package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
interface Action {
    val name: String
    fun processInput(input: Input)
}