package com.aconno.sensorics.data.repository.logs

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity

@Entity(
        tableName = "logs",
        foreignKeys = [(ForeignKey(
                entity = DeviceEntity::class,
                parentColumns = arrayOf("macAddress"),
                childColumns = arrayOf("deviceMacAddress"),
                onDelete = ForeignKey.CASCADE
        ))]
)
data class LogEntity(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                     var info: String,
                     var timestamp:Long,
                     var loggingLevel: Int,
                     var deviceMacAddress: String)