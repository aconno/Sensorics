package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralGooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePublishDataMapper @Inject constructor() : PublishEntityMapper<GooglePublish, GooglePublishEntity> {

    override fun toEntity(data: GooglePublish): GooglePublishEntity {
        return GooglePublishEntity(
            data.id,
            data.name,
            data.projectId,
            data.region,
            data.deviceRegistry,
            data.device,
            data.privateKey,
            data.enabled,
            data.timeType,
            data.timeMillis,
            data.lastTimeMillis,
            data.dataString
        )
    }

    override fun fromEntity(entity: GooglePublishEntity): GooglePublish {
        return GeneralGooglePublish(
            entity.id,
            entity.name,
            entity.projectId,
            entity.region,
            entity.deviceRegistry,
            entity.device,
            entity.privateKey,
            entity.enabled,
            entity.timeType,
            entity.timeMillis,
            entity.lastTimeMillis,
            entity.dataString
        )
    }
}