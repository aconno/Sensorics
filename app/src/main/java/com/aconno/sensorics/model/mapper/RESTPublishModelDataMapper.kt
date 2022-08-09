package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRestPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.model.RestPublishModel
import javax.inject.Inject

class RESTPublishModelDataMapper @Inject constructor() {
    /**
     * Transform a [RestPublishModel] into an [RestPublish].
     *
     * @param restPublishModel Object to be transformed.
     * @return [GeneralRestPublish]
     */
    fun transform(restPublishModel: RestPublishModel): RestPublish {
        return GeneralRestPublish(
            restPublishModel.id,
            restPublishModel.name,
            restPublishModel.url,
            restPublishModel.method,
            restPublishModel.enabled,
            restPublishModel.timeType,
            restPublishModel.timeMillis,
            restPublishModel.lastTimeMillis,
            restPublishModel.dataString
        )
    }
}