package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "google_publish")
data class GooglePublishEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var projectId: String,
    var region: String,
    var deviceRegistry: String,
    var device: String,
    var privateKey: String,
    var enabled: Boolean,
    var timeType: String,
    var timeMillis: Long,
    var lastTimeMillis: Long
)