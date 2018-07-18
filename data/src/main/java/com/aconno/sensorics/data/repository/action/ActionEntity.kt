package com.aconno.sensorics.data.repository.action

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "actions")
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
    var outcomeType: Int,
    var phoneNumber: String
)