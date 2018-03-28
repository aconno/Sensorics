package com.aconno.acnsensa.domain.ifttt

interface Input {
    val value: Float
    val type: Int
    val timestamp: Long
}