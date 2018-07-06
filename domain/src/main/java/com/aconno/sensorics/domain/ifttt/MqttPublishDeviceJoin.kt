package com.aconno.sensorics.domain.ifttt

interface MqttPublishDeviceJoin : PublishDeviceJoin {
    val mId: Long
    override val dId: String
}