package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class GeneralAzureMqttPublish(
    override val id: Long,
    override val name: String,
    override val iotHubName: String,
    override val deviceId: String,
    override val sharedAccessKey: String,
    override var enabled: Boolean,
    override var timeType: String,
    override var timeMillis: Long,
    override var lastTimeMillis: Long,
    override var dataString: String
) : AzureMqttPublish {
    override val type: PublishType = PublishType.AZURE_MQTT
}