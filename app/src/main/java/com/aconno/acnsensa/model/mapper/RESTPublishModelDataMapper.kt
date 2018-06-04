package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.ifttt.GeneralRESTPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.model.RESTPublishModel
import javax.inject.Inject
import javax.inject.Singleton

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
            restPublishModel.lastTimeMillis

        )
    }
}