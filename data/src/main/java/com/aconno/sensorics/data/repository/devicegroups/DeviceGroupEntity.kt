package com.aconno.sensorics.data.repository.devicegroups

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_groups")
data class DeviceGroupEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String
)