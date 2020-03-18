package com.aconno.sensorics.domain.ifttt

interface AzureMqttPublishDeviceJoin : PublishDeviceJoin {
    val mId: Long
    override val dId: String
}