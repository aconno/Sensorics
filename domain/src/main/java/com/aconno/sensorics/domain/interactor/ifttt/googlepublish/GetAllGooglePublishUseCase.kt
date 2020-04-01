package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishUseCase

class GetAllGooglePublishUseCase(
    googlePublishRepository: GooglePublishRepository
) : GetAllPublishUseCase<GooglePublish>(googlePublishRepository)