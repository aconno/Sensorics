package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.model.GooglePublishModel
import javax.inject.Inject
import javax.inject.Singleton

class GooglePublishDataMapper @Inject constructor() {


    /**
     * Transform a [GooglePublish] into an [GooglePublishModel].
     *
     * @param googlePublish Object to be transformed.
     * @return [GooglePublishModel]
     */
    fun transform(googlePublish: GooglePublish): GooglePublishModel {
        return GooglePublishModel(
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
            googlePublish.lastTimeMillis
        )
    }

    /**
     * Transform a List of [GooglePublish] into a Collection of [GooglePublishModel].
     *
     * @param googlePublishCollection Object Collection to be transformed.
     * @return [GooglePublishModel]
     */
    fun transform(googlePublishCollection: Collection<GooglePublish>): List<GooglePublishModel> {
        val googlePublishModelList = mutableListOf<GooglePublishModel>()
        for (googlePublish in googlePublishCollection) {
            val user = transform(googlePublish)
            googlePublishModelList.add(user)
        }
        return googlePublishModelList
    }
}