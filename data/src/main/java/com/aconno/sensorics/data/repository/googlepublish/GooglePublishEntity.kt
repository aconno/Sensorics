package com.aconno.sensorics.data.repository.googlepublish

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var lastTimeMillis: Long,
    var dataString: String
)