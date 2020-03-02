package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository

class UpdatePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository,
    private val mqttPublishRepository: MqttPublishRepository,
    private val azureMqttPublishRepository: AzureMqttPublishRepository,
    private val restPublishRepository: RestPublishRepository
) {
    fun execute(parameter: BasePublish) =
        when (parameter) {
            is GooglePublish -> googlePublishRepository.updatePublish(parameter)
            is MqttPublish -> mqttPublishRepository.updatePublish(parameter)
            is AzureMqttPublish -> azureMqttPublishRepository.updatePublish(parameter)
            is RestPublish -> restPublishRepository.updatePublish(parameter)
            else -> throw IllegalArgumentException("Unexpected publish type.")
        }
}