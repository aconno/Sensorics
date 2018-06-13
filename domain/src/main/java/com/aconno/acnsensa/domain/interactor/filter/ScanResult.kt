package com.aconno.acnsensa.domain.interactor.filter

data class ScanResult(
    val timestamp: Long,
    val macAddress: String,
    val rawData: List<Byte>
)