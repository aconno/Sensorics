package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeletePublishUseCase

class DeleteAzureMqttPublishUseCase(
    azureMqttPublishRepository: AzureMqttPublishRepository
) : DeletePublishUseCase<AzureMqttPublish>(azureMqttPublishRepository)