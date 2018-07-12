package com.aconno.sensorics.domain.model

data class ScanResult(
    val timestamp: Long,
    val macAddress: String,
    val rssi: Int,
    val rawData: List<Byte>
)