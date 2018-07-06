package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.gpublish.GooglePublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralGooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePublishEntityDataMapper @Inject constructor() {

    /**
     * Transform a [GooglePublishEntity] into an [GooglePublish].
     *
     * @param googlePublishEntity Object to be transformed.
     * @return [GooglePublish]
     */
    fun transform(googlePublishEntity: GooglePublishEntity): GooglePublish {
        return GeneralGooglePublish(
            googlePublishEntity.id,
            googlePublishEntity.name,
            googlePublishEntity.projectId,
            googlePublishEntity.region,
            googlePublishEntity.deviceRegistry,
            googlePublishEntity.device,
            googlePublishEntity.privateKey,
            googlePublishEntity.enabled,
            googlePublishEntity.timeType,
            googlePublishEntity.timeMillis,
            googlePublishEntity.lastTimeMillis,
            googlePublishEntity.dataString

        )
    }

    /**
     * Transform a List of [GooglePublishEntity] into a Collection of [GooglePublish].
     *
     * @param googlePublishEntityCollection Object Collection to be transformed.
     * @return [GooglePublish]
     */
    fun transform(googlePublishEntityCollection: Collection<GooglePublishEntity>): List<GooglePublish> {
        val googlePublishList = mutableListOf<GooglePublish>()
        for (googlePublishEntity in googlePublishEntityCollection) {
            val user = transform(googlePublishEntity)
            googlePublishList.add(user)
        }
        return googlePublishList
    }
}