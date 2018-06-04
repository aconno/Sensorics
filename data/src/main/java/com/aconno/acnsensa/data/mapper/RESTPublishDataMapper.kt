package com.aconno.acnsensa.data.mapper

import com.aconno.acnsensa.data.repository.RESTPublishEntity
import com.aconno.acnsensa.domain.ifttt.RESTPublish
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
            restPublish.enabled,
            restPublish.timeType,
            restPublish.timeMillis,
            restPublish.lastTimeMillis

        )
    }
}
