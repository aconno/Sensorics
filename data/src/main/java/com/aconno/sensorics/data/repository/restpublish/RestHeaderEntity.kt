package com.aconno.sensorics.data.repository.restpublish

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "rest_headers",
    foreignKeys =
    [(ForeignKey(
        entity = RestPublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("rId"),
        onDelete = ForeignKey.CASCADE
    ))]
)
class RestHeaderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val rId: Long,
    val key: String,
    val value: String
)