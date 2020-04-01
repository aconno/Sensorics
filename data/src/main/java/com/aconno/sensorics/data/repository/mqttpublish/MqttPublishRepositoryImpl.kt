package com.aconno.sensorics.data.repository.mqttpublish

import com.aconno.sensorics.data.mapper.MqttPublishDataMapper
import com.aconno.sensorics.data.repository.PublishRepositoryImpl
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository

class MqttPublishRepositoryImpl(
    mqttPublishDao: MqttPublishDao,
    mqttPublishDataMapper: MqttPublishDataMapper
) : PublishRepositoryImpl<MqttPublish, MqttPublishEntity>(
    mqttPublishDao,
    mqttPublishDataMapper
), MqttPublishRepository