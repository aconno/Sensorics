package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddPublishUseCase

class AddMqttPublishUseCase(
    mqttPublishRepository: MqttPublishRepository
) : AddPublishUseCase<MqttPublish>(mqttPublishRepository)