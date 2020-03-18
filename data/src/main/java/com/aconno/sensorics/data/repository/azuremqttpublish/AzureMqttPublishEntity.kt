package com.aconno.sensorics.data.repository.azuremqttpublish

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azure_mqtt_publish")
data class AzureMqttPublishEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var iotHubName: String,
    var deviceId: String,
    var sharedAccessKey: String,
    var enabled: Boolean,
    var timeType: String,
    var timeMillis: Long,
    var lastTimeMillis: Long,
    var dataString: String

)