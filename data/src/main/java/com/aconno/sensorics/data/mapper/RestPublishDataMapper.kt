package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralRestPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestPublishDataMapper @Inject constructor() : PublishEntityMapper<RestPublish, RestPublishEntity> {

    override fun toEntity(data: RestPublish): RestPublishEntity {
        return RestPublishEntity(
            data.id,
            data.name,
            data.url,
            data.method,
            data.enabled,
            data.timeType,
            data.timeMillis,
            data.lastTimeMillis,
            data.dataString
        )
    }


    override fun fromEntity(entity: RestPublishEntity): RestPublish {
        return GeneralRestPublish(
            entity.id,
            entity.name,
            entity.url,
            entity.method,
            entity.enabled,
            entity.timeType,
            entity.timeMillis,
            entity.lastTimeMillis,
            entity.dataString
        )
    }
}
