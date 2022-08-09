package com.aconno.sensorics.domain.ifttt

class GeneralMqttPublishDeviceJoin(
    override val publishId: Long,
    override val deviceId: String
) : MqttPublishDeviceJoin