package com.aconno.acnsensa.domain.ifttt

interface Condition {
    fun isSatisfied(input: Input): Boolean
}