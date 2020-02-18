package com.aconno.sensorics.data.repository.publishdevicejoin

import androidx.room.Entity
import androidx.room.ForeignKey
import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishEntity
import com.aconno.sensorics.data.repository.devices.DeviceEntity

@Entity(
    tableName = "azure_mqtt_publish_device_join",
    primaryKeys = ["aId", "dId"],
    foreignKeys =
    [(ForeignKey(
        entity = AzureMqttPublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("aId"),
        onDelete = ForeignKey.CASCADE
    )), ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("dId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class AzureMqttPublishDeviceJoinEntity(
    val aId: Long,
    val dId: String
)