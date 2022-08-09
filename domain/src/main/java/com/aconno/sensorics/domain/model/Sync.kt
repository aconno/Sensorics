package com.aconno.sensorics.domain.model

data class Sync(
    val publisherUniqueId: String,
    val macAddress: String,
    val advertisementId: String,
    val lastSyncTimestamp: Long
)