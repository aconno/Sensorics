package com.aconno.sensorics.data.repository.action

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aconno.sensorics.data.repository.devices.DeviceEntity

@Entity(
    tableName = "actions",
    foreignKeys = [(ForeignKey(
        entity = DeviceEntity::class,
        parentColumns = arrayOf("macAddress"),
        childColumns = arrayOf("deviceMacAddress"),
        onDelete = ForeignKey.CASCADE
    ))]
)
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var deviceName: String,
    var deviceAlias: String,
    var deviceMacAddress: String,
    var deviceIcon: String,
    var readingType: String,
    var conditionType: Int,
    var value: Float,
    var textMessage: String,
    var outcomeType: Int
)