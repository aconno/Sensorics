package com.aconno.sensorics.data.repository.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync")
data class SyncEntity(
    @PrimaryKey
    var publisherUniqueId: String,
    var macAddress: String,
    var advertisementId: String,
    var lastSyncTimestamp: Long
)