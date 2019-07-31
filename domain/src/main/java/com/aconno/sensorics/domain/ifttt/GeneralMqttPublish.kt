package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class GeneralMqttPublish(
    override val id: Long,
    override val name: String,
    override val url: String,
    override val clientId: String,
    override val username: String,
    override val password: String,
    override val topic: String,
    override val qos: Int,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : MqttPublish {
    override val type: PublishType = PublishType.MQTT
}