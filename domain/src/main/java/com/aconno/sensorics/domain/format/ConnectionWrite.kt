package com.aconno.sensorics.domain.format

data class ConnectionWrite(
    val serviceUUID: String,
    val characteristicUUID: String,
    val values: List<Value>
)