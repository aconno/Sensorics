package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.*

class UpdatePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository,
    private val mqttPublishRepository: MqttPublishRepository,
    private val azureMqttPublishRepository: AzureMqttPublishRepository,
    private val restPublishRepository: RestPublishRepository
) {
    fun execute(parameter: BasePublish) =
        when (parameter) {
            is GooglePublish -> googlePublishRepository.updateGooglePublish(parameter)
            is MqttPublish -> mqttPublishRepository.updateMqttPublish(parameter)
            is AzureMqttPublish -> azureMqttPublishRepository.updateAzureMqttPublish(parameter)
            is RestPublish -> restPublishRepository.updateRESTPublish(parameter)
            else -> throw IllegalArgumentException("Unexpected publish type.")
        }
}