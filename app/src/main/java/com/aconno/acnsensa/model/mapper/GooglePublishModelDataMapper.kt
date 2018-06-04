package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.model.GooglePublishModel
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
            googlePublishModel.lastTimeMillis

        )
    }
}