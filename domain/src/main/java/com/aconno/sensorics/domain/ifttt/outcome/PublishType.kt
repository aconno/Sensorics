package com.aconno.sensorics.domain.ifttt.outcome

enum class PublishType(val type: String) {
    MQTT("mqtt"), GOOGLE("google"), REST("rest"), AZURE_MQTT("azure")
}