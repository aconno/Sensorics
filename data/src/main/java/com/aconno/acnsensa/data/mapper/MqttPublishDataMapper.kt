package com.aconno.acnsensa.data.mapper

import com.aconno.acnsensa.data.repository.mpublish.MqttPublishEntity
import com.aconno.acnsensa.domain.ifttt.GeneralMqttPublish
import com.aconno.acnsensa.domain.ifttt.MqttPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttPublishDataMapper @Inject constructor() {

    fun toMqttPublish(mqttPublishEntity: MqttPublishEntity): MqttPublish {
        return GeneralMqttPublish(
            mqttPublishEntity.id,
            mqttPublishEntity.name,
            mqttPublishEntity.url,
            mqttPublishEntity.clientId,
            mqttPublishEntity.username,
            mqttPublishEntity.password,
            mqttPublishEntity.topic,
            mqttPublishEntity.qos,
            mqttPublishEntity.enabled,
            mqttPublishEntity.timeType,
            mqttPublishEntity.timeMillis,
            mqttPublishEntity.lastTimeMillis
        )
    }

    fun toMqttPublishEntity(mqttPublish: MqttPublish): MqttPublishEntity {
        return MqttPublishEntity(
            mqttPublish.id,
            mqttPublish.name,
            mqttPublish.url,
            mqttPublish.clientId,
            mqttPublish.username,
            mqttPublish.password,
            mqttPublish.topic,
            mqttPublish.qos,
            mqttPublish.enabled,
            mqttPublish.timeType,
            mqttPublish.timeMillis,
            mqttPublish.lastTimeMillis
        )
    }

    fun toMqttPublishList(mqttPublishEntityCollection: Collection<MqttPublishEntity>): List<MqttPublish> {
        val mqttPublishList = mutableListOf<MqttPublish>()
        for (mqttPublishEntity in mqttPublishEntityCollection) {
            val user = toMqttPublish(mqttPublishEntity)
            mqttPublishList.add(user)
        }
        return mqttPublishList
    }
}