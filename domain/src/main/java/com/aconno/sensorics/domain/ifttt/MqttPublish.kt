package com.aconno.sensorics.domain.ifttt

interface MqttPublish : BasePublish {
    val url: String
    val clientId: String
    val username: String
    val password: String
    val topic: String
    val qos: Int
}