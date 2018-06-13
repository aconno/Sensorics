package com.aconno.acnsensa.domain.ifttt

interface MqttPublish : BasePublish {
    val url: String
    val clientId: String
    val username: String
    val password: String
}