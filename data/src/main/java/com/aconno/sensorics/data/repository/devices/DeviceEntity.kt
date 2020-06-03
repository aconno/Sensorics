package com.aconno.sensorics.data.repository.devices

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.aconno.sensorics.data.repository.DatabaseDateConverter
import java.util.*

@Entity(tableName = "devices")
data class DeviceEntity(
    var name: String,
    var alias: String,
    @PrimaryKey var macAddress: String,
    var icon: String,
    var connectable: Boolean = false,
    var timeAdded : Date? = null,
    var deviceVersion : String? = null
)