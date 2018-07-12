package com.aconno.sensorics.domain.model

data class Device(
    val name: String,
    val alias: String,
    val macAddress: String,
    val icon: String = ""
)