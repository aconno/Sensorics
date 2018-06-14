package com.aconno.acnsensa.domain.model

data class ScanResult(
    val timestamp: Long,
    val macAddress: String,
    val rawData: List<Byte>
)