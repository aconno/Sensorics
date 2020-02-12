package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.azuremqttpublish.AzureMqttPublishEntity
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.GeneralAzureMqttPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AzureMqttPublishDataMapper @Inject constructor() {

    fun toAzureMqttPublish(azureMqttPublishEntity: AzureMqttPublishEntity): AzureMqttPublish {
        return GeneralAzureMqttPublish(
                azureMqttPublishEntity.id,
                azureMqttPublishEntity.name,
                azureMqttPublishEntity.iotHubName,
                azureMqttPublishEntity.deviceId,
                azureMqttPublishEntity.sharedAccessKey,
                azureMqttPublishEntity.enabled,
                azureMqttPublishEntity.timeType,
                azureMqttPublishEntity.timeMillis,
                azureMqttPublishEntity.lastTimeMillis,
                azureMqttPublishEntity.dataString
        )
    }

    fun toAzureMqttPublishEntity(azureMqttPublish: AzureMqttPublish): AzureMqttPublishEntity {
        return AzureMqttPublishEntity(
                azureMqttPublish.id,
                azureMqttPublish.name,
                azureMqttPublish.iotHubName,
                azureMqttPublish.deviceId,
                azureMqttPublish.sharedAccessKey,
                azureMqttPublish.enabled,
                azureMqttPublish.timeType,
                azureMqttPublish.timeMillis,
                azureMqttPublish.lastTimeMillis,
                azureMqttPublish.dataString
        )
    }

    fun toAzureMqttPublishList(azureMqttPublishEntities: List<AzureMqttPublishEntity>): List<AzureMqttPublish> {
        val azureMqttPublishList = mutableListOf<AzureMqttPublish>()
        for (azureMqttPublishEntity in azureMqttPublishEntities) {
            val publish = toAzureMqttPublish(azureMqttPublishEntity)
            azureMqttPublishList.add(publish)
        }
        return azureMqttPublishList
    }

}