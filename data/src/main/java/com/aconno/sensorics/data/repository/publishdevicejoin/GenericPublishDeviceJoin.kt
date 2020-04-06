package com.aconno.sensorics.data.repository.publishdevicejoin

import androidx.room.Entity
import androidx.room.ForeignKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity

@Entity(
    tableName = "publish_device_join",
    primaryKeys = ["publishId", "deviceId", "publishType"],
    foreignKeys =
    [ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("deviceId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class GenericPublishDeviceJoinEntity(
    val publishId: Long,
    val deviceId: String,
    val publishType: String
)
