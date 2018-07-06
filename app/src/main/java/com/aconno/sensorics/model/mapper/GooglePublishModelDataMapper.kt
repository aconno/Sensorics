package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralGooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.model.GooglePublishModel
import javax.inject.Inject
import javax.inject.Singleton

class GooglePublishModelDataMapper @Inject constructor() {

    /**
     * Transform a [GooglePublishModel] into an [GooglePublish].
     *
     * @param googlePublishModel Object to be transformed.
     * @return [GooglePublish]
     */
    fun transform(googlePublishModel: GooglePublishModel): GooglePublish {
        return GeneralGooglePublish(
            googlePublishModel.id,
            googlePublishModel.name,
            googlePublishModel.projectId,
            googlePublishModel.region,
            googlePublishModel.deviceRegistry,
            googlePublishModel.device,
            googlePublishModel.privateKey,
            googlePublishModel.enabled,
            googlePublishModel.timeType,
            googlePublishModel.timeMillis,
            googlePublishModel.lastTimeMillis,
            googlePublishModel.dataString
        )
    }
}