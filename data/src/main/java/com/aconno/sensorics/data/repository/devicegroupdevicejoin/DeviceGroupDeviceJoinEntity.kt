package com.aconno.sensorics.data.repository.devicegroupdevicejoin

import androidx.room.Entity
import androidx.room.ForeignKey
import com.aconno.sensorics.data.repository.devicegroups.DeviceGroupEntity
import com.aconno.sensorics.data.repository.devices.DeviceEntity

@Entity(
    tableName = "device_group_device_join",
    primaryKeys = ["deviceId"],
    foreignKeys = [
        ForeignKey(entity = DeviceGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceGroupId"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DeviceEntity::class,
            parentColumns = ["macAddress"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class DeviceGroupDeviceJoinEntity(
    val deviceGroupId: Long,
    val deviceId: String
)