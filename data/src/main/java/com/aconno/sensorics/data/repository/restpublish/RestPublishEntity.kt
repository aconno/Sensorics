package com.aconno.sensorics.data.repository.restpublish

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "rest_publish")
data class RestPublishEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var url: String,
    var method: String,
    var enabled: Boolean,
    var timeType: String,
    var timeMillis: Long,
    var lastTimeMillis: Long,
    var dataString: String
)
