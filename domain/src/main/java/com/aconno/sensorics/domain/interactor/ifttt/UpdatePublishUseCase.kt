package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.RestPublishRepository

class UpdatePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository,
    private val mqttPublishRepository: MqttPublishRepository,
    private val restPublishRepository: RestPublishRepository
) {
}