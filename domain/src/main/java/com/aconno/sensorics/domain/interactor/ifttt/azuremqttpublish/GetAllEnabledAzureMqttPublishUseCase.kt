package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublishRepository
import com.aconno.sensorics.domain.ifttt.BasePublish

class GetAllEnabledAzureMqttPublishUseCase (
        private val azureMqttPublishRepository: AzureMqttPublishRepository
) {
    fun execute(): List<BasePublish> {
        return azureMqttPublishRepository.getAllEnabledAzureMqttPublish()
    }
}