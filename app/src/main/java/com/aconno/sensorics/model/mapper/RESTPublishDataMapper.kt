package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.model.RESTPublishModel
import javax.inject.Inject

class RESTPublishDataMapper @Inject constructor() {


    fun transform(restPublish: RESTPublish): RESTPublishModel {
        return RESTPublishModel(
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

    fun transform(restPublishCollection: Collection<RESTPublish>): List<RESTPublishModel> {
        val restPublishModelList = mutableListOf<RESTPublishModel>()
        for (restPublish in restPublishCollection) {
            val user = transform(restPublish)
            restPublishModelList.add(user)
        }
        return restPublishModelList
    }
}