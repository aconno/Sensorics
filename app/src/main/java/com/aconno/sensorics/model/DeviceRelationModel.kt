package com.aconno.sensorics.model

data class DeviceRelationModel(
    val name: String,
    val macAddress: String,
    val icon: String,
    var related: Boolean = false
)