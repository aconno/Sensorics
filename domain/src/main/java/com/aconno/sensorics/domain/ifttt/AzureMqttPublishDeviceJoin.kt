package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

interface AzureMqttPublishDeviceJoin : PublishDeviceJoin {
    override val publishType: PublishType
        get() = PublishType.AZURE_MQTT
}