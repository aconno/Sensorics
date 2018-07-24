package com.aconno.sensorics.domain.format

data class ConnectionRead(
    val serviceUUID: String,
    val characteristicUUID: String,
    val characteristicName: String
)