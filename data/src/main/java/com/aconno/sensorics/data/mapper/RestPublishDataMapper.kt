package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.domain.ifttt.RestPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestPublishDataMapper @Inject constructor() {


    /**
     * Transform a [RestPublish] into an [RestPublishEntity].
     *
     * @param restPublish Object to be transformed.
     * @return [RestPublishEntity]
     */
    fun transform(restPublish: RestPublish): RestPublishEntity {
        return RestPublishEntity(
            restPublish.id,
            restPublish.name,
            restPublish.url,
            restPublish.method,
            restPublish.enabled,
            restPublish.timeType,
            restPublish.timeMillis,
            restPublish.lastTimeMillis,
            restPublish.dataString
        )
    }
}
