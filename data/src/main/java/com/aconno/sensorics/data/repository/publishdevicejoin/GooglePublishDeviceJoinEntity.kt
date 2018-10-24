package com.aconno.sensorics.data.repository.publishdevicejoin

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity

@Entity(
    tableName = "google_publish_device_join",
    primaryKeys = ["gId", "dId"],
    foreignKeys =
    [ForeignKey(
        entity = GooglePublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("gId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("dId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class GooglePublishDeviceJoinEntity(
    val gId: Long,
    val dId: String
)