package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetPublishByIdUseCase

class GetGooglePublishByIdUseCase(
    googlePublishRepository: GooglePublishRepository
) : GetPublishByIdUseCase<GooglePublish>(googlePublishRepository)