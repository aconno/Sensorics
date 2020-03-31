package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllEnabledPublishUseCase

class GetAllEnabledAzureMqttPublishUseCase(
    azureMqttPublishRepository: AzureMqttPublishRepository
) : GetAllEnabledPublishUseCase<AzureMqttPublish>(azureMqttPublishRepository)