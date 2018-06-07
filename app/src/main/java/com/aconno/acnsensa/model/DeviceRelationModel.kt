package com.aconno.acnsensa.model

data class DeviceRelationModel(
    val name: String,
    val macAddress: String,
    var related: Boolean = false
)