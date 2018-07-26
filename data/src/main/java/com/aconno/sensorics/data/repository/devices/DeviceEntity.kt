package com.aconno.sensorics.data.repository.devices

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    var name: String,
    var alias: String,
    @PrimaryKey var macAddress: String,
    var icon: String,
    var connectable: Boolean = false
)