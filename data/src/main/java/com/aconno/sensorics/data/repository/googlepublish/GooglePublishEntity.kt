package com.aconno.sensorics.data.repository.googlepublish

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aconno.sensorics.data.repository.PublishEntity

@Entity(tableName = "google_publish")
data class GooglePublishEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    override var name: String,
    var projectId: String,
    var region: String,
    var deviceRegistry: String,
    var device: String,
    var privateKey: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : PublishEntity