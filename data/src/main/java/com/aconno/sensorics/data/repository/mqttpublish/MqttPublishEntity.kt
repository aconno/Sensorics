package com.aconno.sensorics.data.repository.mqttpublish

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aconno.sensorics.data.repository.PublishEntity

@Entity(tableName = "mqtt_publish")
data class MqttPublishEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    override var name: String,
    var url: String,
    var clientId: String,
    var username: String,
    var password: String,
    var topic: String,
    var qos: Int,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : PublishEntity