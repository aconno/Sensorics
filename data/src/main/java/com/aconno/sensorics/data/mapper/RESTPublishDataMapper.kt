package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.rpublish.RESTPublishEntity
import com.aconno.sensorics.domain.ifttt.RESTPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RESTPublishDataMapper @Inject constructor() {


    /**
     * Transform a [RESTPublish] into an [RESTPublishEntity].
     *
     * @param restPublish Object to be transformed.
     * @return [RESTPublishEntity]
     */
    fun transform(restPublish: RESTPublish): RESTPublishEntity {
        return RESTPublishEntity(
            restPublish.id,
            restPublish.name,
            restPublish.url,
            restPublish.method,
            restPublish.parameterName,
            restPublish.enabled,
            restPublish.timeType,
            restPublish.timeMillis,
            restPublish.lastTimeMillis,
            restPublish.dataString
        )
    }
}
