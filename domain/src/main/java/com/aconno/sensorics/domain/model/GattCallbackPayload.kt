package com.aconno.sensorics.domain.model

class GattCallbackPayload(
    val action: String,
    val payload: Any? = null
)