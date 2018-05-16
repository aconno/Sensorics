package com.aconno.acnsensa.domain.ifttt

interface Condition {
    val sensorType: Int
    val limit: Float
    val type: Int
    fun isSatisfied(input: Input): Boolean
}