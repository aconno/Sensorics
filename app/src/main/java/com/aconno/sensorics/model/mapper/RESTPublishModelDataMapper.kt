package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRESTPublish
import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.model.RESTPublishModel
import javax.inject.Inject

class RESTPublishModelDataMapper @Inject constructor() {
    /**
     * Transform a [RESTPublishModel] into an [RESTPublish].
     *
     * @param restPublishModel Object to be transformed.
     * @return [GeneralRESTPublish]
     */
    fun transform(restPublishModel: RESTPublishModel): RESTPublish {
        return GeneralRESTPublish(
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