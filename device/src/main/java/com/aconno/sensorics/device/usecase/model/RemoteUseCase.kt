package com.aconno.sensorics.device.usecase.model

data class RemoteUseCase(
    val name: String,
    val serverTimestamp: Long,
    val html: String
)