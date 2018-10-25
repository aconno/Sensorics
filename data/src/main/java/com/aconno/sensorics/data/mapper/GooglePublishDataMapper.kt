package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.googlepublish.GooglePublishEntity
import com.aconno.sensorics.domain.ifttt.GooglePublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePublishDataMapper @Inject constructor() {

    /**
     * Transform a [GooglePublish] into an [GooglePublishEntity].
     *
     * @param googlePublish Object to be transformed.
     * @return [GooglePublishEntity]
     */
    fun transform(googlePublish: GooglePublish): GooglePublishEntity {
        return GooglePublishEntity(
            googlePublish.id,
            googlePublish.name,
            googlePublish.projectId,
            googlePublish.region,
            googlePublish.deviceRegistry,
            googlePublish.device,
            googlePublish.privateKey,
            googlePublish.enabled,
            googlePublish.timeType,
            googlePublish.timeMillis,
            googlePublish.lastTimeMillis,
            googlePublish.dataString
        )
    }
}