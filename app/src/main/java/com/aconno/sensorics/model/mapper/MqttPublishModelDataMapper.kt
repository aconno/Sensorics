package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralMqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.model.MqttPublishModel
import javax.inject.Inject

class MqttPublishModelDataMapper @Inject constructor() {

    fun toMqttPublish(mqttPublishModel: MqttPublishModel): MqttPublish {
        return GeneralMqttPublish(
            mqttPublishModel.id,
            mqttPublishModel.name,
            mqttPublishModel.url,
            mqttPublishModel.clientId,
            mqttPublishModel.username,
            mqttPublishModel.password,
            mqttPublishModel.topic,
            mqttPublishModel.qos,
            mqttPublishModel.enabled,
            mqttPublishModel.timeType,
            mqttPublishModel.timeMillis,
            mqttPublishModel.lastTimeMillis,
            mqttPublishModel.dataString
        )
    }

    fun toMqttPublishModel(mqttPublish: MqttPublish): MqttPublishModel {
        return MqttPublishModel(
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
            mqttPublish.lastTimeMillis,
            mqttPublish.dataString
        )
    }

    fun toMqttPublishModelList(mqttPublishCollection: Collection<MqttPublish>): List<MqttPublishModel> {
        val mqttPublishModelList = mutableListOf<MqttPublishModel>()
        for (mqttPublish in mqttPublishCollection) {
            val user = toMqttPublishModel(mqttPublish)
            mqttPublishModelList.add(user)
        }
        return mqttPublishModelList
    }
}