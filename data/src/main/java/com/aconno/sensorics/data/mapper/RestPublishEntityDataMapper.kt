package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.restpublish.RestPublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralRestPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestPublishEntityDataMapper @Inject constructor() {

    /**
     * Transform a [RestPublishEntity] into an [RestPublish].
     *
     * @param restPublishEntity Object to be transformed.
     * @return [RestPublish]
     */
    fun transform(restPublishEntity: RestPublishEntity): RestPublish {
        return GeneralRestPublish(
            restPublishEntity.id,
            restPublishEntity.name,
            restPublishEntity.url,
            restPublishEntity.method,
            restPublishEntity.enabled,
            restPublishEntity.timeType,
            restPublishEntity.timeMillis,
            restPublishEntity.lastTimeMillis,
            restPublishEntity.dataString

        )
    }

    /**
     * Transform a List of [RestPublishEntity] into a Collection of [RestPublish].
     *
     * @param restPublishEntityCollection Object Collection to be transformed.
     * @return [RestPublish]
     */
    fun transform(restPublishEntityCollection: Collection<RestPublishEntity>): List<RestPublish> {
        val restPublishList = mutableListOf<RestPublish>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = transform(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }
}