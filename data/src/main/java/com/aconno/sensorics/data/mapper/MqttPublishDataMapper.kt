package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralMqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttPublishDataMapper @Inject constructor() : PublishEntityMapper<MqttPublish, MqttPublishEntity> {
    override fun toEntity(data: MqttPublish): MqttPublishEntity {
        return MqttPublishEntity(
            data.id,
            data.name,
            data.url,
            data.clientId,
            data.username,
            data.password,
            data.topic,
            data.qos,
            data.enabled,
            data.timeType,
            data.timeMillis,
            data.lastTimeMillis,
            data.dataString
        )
    }

    override fun fromEntity(entity: MqttPublishEntity): MqttPublish {
        return GeneralMqttPublish(
            entity.id,
            entity.name,
            entity.url,
            entity.clientId,
            entity.username,
            entity.password,
            entity.topic,
            entity.qos,
            entity.enabled,
            entity.timeType,
            entity.timeMillis,
            entity.lastTimeMillis,
            entity.dataString
        )
    }
}