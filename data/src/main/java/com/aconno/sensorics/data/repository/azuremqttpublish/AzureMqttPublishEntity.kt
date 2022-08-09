package com.aconno.sensorics.data.repository.azuremqttpublish

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aconno.sensorics.data.repository.PublishEntity

@Entity(tableName = "azure_mqtt_publish")
data class AzureMqttPublishEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    override var name: String,
    var iotHubName: String,
    var deviceId: String,
    var sharedAccessKey: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : PublishEntity