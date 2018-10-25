package com.aconno.sensorics.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface MqttPublishRepository {
    fun addMqttPublish(mqttPublish: MqttPublish): Long
    fun updateMqttPublish(mqttPublish: MqttPublish)
    fun deleteMqttPublish(mqttPublish: MqttPublish)
    fun getAllMqttPublish(): Single<List<BasePublish>>
    fun getAllEnabledMqttPublish(): List<BasePublish>
    fun getMqttPublishById(mqttPublishId: Long): Maybe<MqttPublish>
}
