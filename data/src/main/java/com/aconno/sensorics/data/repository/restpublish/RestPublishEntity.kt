package com.aconno.sensorics.data.repository.restpublish

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aconno.sensorics.data.repository.PublishEntity


@Entity(tableName = "rest_publish")
data class RestPublishEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Long,
    override var name: String,
    var url: String,
    var method: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : PublishEntity
