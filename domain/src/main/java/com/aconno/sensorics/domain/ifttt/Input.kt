package com.aconno.sensorics.domain.ifttt

interface Input {
    val macAddress: String
    val value: Float
    val type: String
    val timestamp: Long
}