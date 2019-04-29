package com.aconno.sensorics.domain.model

data class ResourceConfig(
    val deviceScreenPath: String,
    val formatPath: String,
    val iconPath: String,
    val id: String,
    val name: String,
    val usecaseScreenPath: String,
    val connectionScreenPath: String? = null
)