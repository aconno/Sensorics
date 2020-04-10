package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

interface MqttPublishDeviceJoin : PublishDeviceJoin {
    override val publishType: PublishType
        get() = PublishType.MQTT
}