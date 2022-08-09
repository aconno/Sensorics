package com.aconno.sensorics.domain.ifttt

interface AzureMqttPublish : BasePublish {
    val iotHubName: String
    val deviceId: String
    val sharedAccessKey: String
}