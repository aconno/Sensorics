package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetPublishByIdUseCase

class GetRestPublishByIdUseCase(
    restPublishRepository: RestPublishRepository
) : GetPublishByIdUseCase<RestPublish>(restPublishRepository)