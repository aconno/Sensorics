package com.aconno.acnsensa.domain.ifttt

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
    override var lastTimeMillis: Long
) : MqttPublish