package com.aconno.sensorics.model

data class DeviceRelationModel(
    val name: String,
    val alias: String,
    val macAddress: String,
    val icon: String,
    var related: Boolean = false
)