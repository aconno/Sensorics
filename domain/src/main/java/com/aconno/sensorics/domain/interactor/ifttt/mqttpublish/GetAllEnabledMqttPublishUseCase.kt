package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository

class GetAllEnabledMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) {
    fun execute(): List<BasePublish> {
        return mqttPublishRepository.getAllEnabledMqttPublish()
    }
}