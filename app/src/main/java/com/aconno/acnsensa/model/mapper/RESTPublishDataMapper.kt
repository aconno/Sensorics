package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import javax.inject.Inject
import javax.inject.Singleton

class RESTPublishDataMapper @Inject constructor() {


    /**
     * Transform a [GooglePublish] into an [GooglePublishModel].
     *
     * @param googlePublish Object to be transformed.
     * @return [GooglePublishModel]
     */
    fun transform(restPublish: RESTPublish): RESTPublishModel {
        return RESTPublishModel(
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

    /**
     * Transform a List of [RESTPublish] into a Collection of [RESTPublishModel].
     *
     * @param restPublishCollection Object Collection to be transformed.
     * @return [RESTPublishModel]
     */
    fun transform(restPublishCollection: Collection<RESTPublish>): List<RESTPublishModel> {
        val restPublishModelList = mutableListOf<RESTPublishModel>()
        for (restPublish in restPublishCollection) {
            val user = transform(restPublish)
            restPublishModelList.add(user)
        }
        return restPublishModelList
    }
}