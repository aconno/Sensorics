package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeletePublishUseCase

class DeleteMqttPublishUseCase(
    mqttPublishRepository: MqttPublishRepository
) : DeletePublishUseCase<MqttPublish>(mqttPublishRepository)