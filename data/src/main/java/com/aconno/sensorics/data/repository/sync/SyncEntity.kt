package com.aconno.sensorics.data.repository.sync

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "sync")
data class SyncEntity(
    @PrimaryKey
    var publisherUniqueId: String,
    var macAddress: String,
    var advertisementId: String,
    var lastSyncTimestamp: Long
)