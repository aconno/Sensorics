package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishEntity
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.GeneralAzureMqttPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AzureMqttPublishDataMapper @Inject constructor() : PublishEntityMapper<AzureMqttPublish, AzureMqttPublishEntity> {

    override fun toEntity(data: AzureMqttPublish): AzureMqttPublishEntity {
        return AzureMqttPublishEntity(
            data.id,
            data.name,
            data.iotHubName,
            data.deviceId,
            data.sharedAccessKey,
            data.enabled,
            data.timeType,
            data.timeMillis,
            data.lastTimeMillis,
            data.dataString
        )
    }

    override fun fromEntity(entity: AzureMqttPublishEntity): AzureMqttPublish {
        return GeneralAzureMqttPublish(
            entity.id,
            entity.name,
            entity.iotHubName,
            entity.deviceId,
            entity.sharedAccessKey,
            entity.enabled,
            entity.timeType,
            entity.timeMillis,
            entity.lastTimeMillis,
            entity.dataString
        )
    }
}