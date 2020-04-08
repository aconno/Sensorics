package com.aconno.sensorics.model

import com.aconno.sensorics.domain.ifttt.outcome.PublishType

class AzureMqttPublishModel(
    id: Long,
    name: String,
    val iotHubName: String,
    val deviceId: String,
    val sharedAccessKey: String,
    enabled: Boolean,
    timeType: String,
    timeMillis: Long,
    lastTimeMillis: Long,
    dataString: String
) : BasePublishModel(id, PublishType.AZURE_MQTT, name, enabled, timeType, timeMillis, lastTimeMillis, dataString)