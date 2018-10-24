package com.aconno.sensorics.data.repository.publishdevicejoin

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishEntity

@Entity(
    tableName = "mqtt_publish_device_join",
    primaryKeys = ["mId", "dId"],
    foreignKeys =
    [(ForeignKey(
        entity = MqttPublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("mId"),
        onDelete = ForeignKey.CASCADE
    )), ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("dId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MqttPublishDeviceJoinEntity(
    val mId: Long,
    val dId: String
)