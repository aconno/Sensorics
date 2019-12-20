package com.aconno.sensorics.data.repository.mqttvirtualscanningsource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mqtt_virtual_scanning_source")
data class MqttVirtualScanningSourceEntity(
        @PrimaryKey(autoGenerate = true) var id: Long,
        var name: String,
        var enabled: Boolean
//TODO add other params
)