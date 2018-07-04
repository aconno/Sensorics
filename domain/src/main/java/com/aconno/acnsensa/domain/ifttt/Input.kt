package com.aconno.acnsensa.domain.ifttt

interface Input {
    val macAddress: String
    val value: Float
    val type: String
    val timestamp: Long
}