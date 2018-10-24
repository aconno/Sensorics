package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.model.RestPublishModel
import javax.inject.Inject

class RESTPublishDataMapper @Inject constructor() {


    fun transform(restPublish: RestPublish): RestPublishModel {
        return RestPublishModel(
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

    fun transform(restPublishCollection: Collection<RestPublish>): List<RestPublishModel> {
        val restPublishModelList = mutableListOf<RestPublishModel>()
        for (restPublish in restPublishCollection) {
            val user = transform(restPublish)
            restPublishModelList.add(user)
        }
        return restPublishModelList
    }
}