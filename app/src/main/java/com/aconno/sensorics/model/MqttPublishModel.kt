package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class MqttPublishModel(
    id: Long,
    name: String,
    val url: String,
    val clientId: String,
    val username: String,
    val password: String,
    val topic: String,
    val qos: Int,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long,
    dataString: String
) : BasePublishModel(id, PublishType.MQTT, name, enabled, timeType, timeMillis, lastTimeMillis, dataString)