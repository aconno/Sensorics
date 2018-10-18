package com.aconno.sensorics.data.repository.format

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "formats")
class FormatEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val contentJson: String
)