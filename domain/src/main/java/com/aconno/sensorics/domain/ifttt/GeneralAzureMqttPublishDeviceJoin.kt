package com.aconno.sensorics.domain.ifttt

class GeneralAzureMqttPublishDeviceJoin(
    override val publishId: Long,
    override val deviceId: String
) : AzureMqttPublishDeviceJoin