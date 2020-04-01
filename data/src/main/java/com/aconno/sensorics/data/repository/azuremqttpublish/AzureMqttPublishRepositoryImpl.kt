package com.aconno.sensorics.data.repository.azuremqttpublish

import com.aconno.sensorics.data.mapper.AzureMqttPublishDataMapper
import com.aconno.sensorics.data.repository.PublishRepositoryImpl
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository

class AzureMqttPublishRepositoryImpl(
    azureMqttPublishDao: AzureMqttPublishDao,
    azureMqttPublishDataMapper: AzureMqttPublishDataMapper
) : PublishRepositoryImpl<AzureMqttPublish, AzureMqttPublishEntity>(
    azureMqttPublishDao,
    azureMqttPublishDataMapper
), AzureMqttPublishRepository
