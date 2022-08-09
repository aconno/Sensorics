package com.aconno.sensorics.ui.devicecon

import java.util.*

data class WriteCommand(
    val serviceUUID: UUID,
    val charUUID: UUID,
    val type: String,
    val value: Any
)