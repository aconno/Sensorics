package com.aconno.acnsensa.domain.ifttt

interface MqttPublishDeviceJoin : PublishDeviceJoin {
    val mId: Long
    override val dId: String
}