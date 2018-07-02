package com.aconno.acnsensa.data.repository.pdjoin

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.aconno.acnsensa.data.repository.devices.DeviceEntity
import com.aconno.acnsensa.data.repository.mpublish.MqttPublishEntity

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