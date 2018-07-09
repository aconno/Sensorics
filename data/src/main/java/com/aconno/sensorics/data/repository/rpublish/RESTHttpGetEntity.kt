package com.aconno.sensorics.data.repository.rpublish

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(
    tableName = "rest_headers",
    foreignKeys =
    [(ForeignKey(
        entity = RESTPublishEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("rId"),
        onDelete = ForeignKey.CASCADE
    ))]
)
class RESTHttpGetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val rId: Long,
    val key: String,
    val value: String
)