package com.aconno.sensorics.data.repository.mqttpublish

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mqtt_publish")
data class MqttPublishEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var url: String,
    var clientId: String,
    var username: String,
    var password: String,
    var topic: String,
    var qos: Int,
    var enabled: Boolean,
    var timeType: String,
    var timeMillis: Long,
    var lastTimeMillis: Long,
    var dataString: String
)