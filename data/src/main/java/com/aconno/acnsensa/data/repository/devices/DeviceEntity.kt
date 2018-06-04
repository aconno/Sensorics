package com.aconno.acnsensa.data.repository.devices

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    var name: String,
    @PrimaryKey var macAddress: String
)