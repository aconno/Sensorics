package com.aconno.sensorics.data.repository.pdjoin

import android.arch.persistence.room.ForeignKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity
import com.aconno.sensorics.data.repository.rpublish.RESTPublishEntity

@android.arch.persistence.room.Entity(
    tableName = "rest_publish_device_join",
    primaryKeys = ["rId", "dId"],
    foreignKeys =
    [(ForeignKey(
        entity = RESTPublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("rId"),
        onDelete = ForeignKey.CASCADE
    )), (ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("dId"),
        onDelete = ForeignKey.CASCADE
    ))]
)
class RestPublishDeviceJoinEntity(
    val rId: Long,
    val dId: String
)