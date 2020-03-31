package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetPublishByIdUseCase

class GetMqttPublishByIdUseCase(
    mqttPublishRepository: MqttPublishRepository
) : GetPublishByIdUseCase<MqttPublish>(mqttPublishRepository)