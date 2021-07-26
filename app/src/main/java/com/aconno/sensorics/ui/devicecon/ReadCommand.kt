package com.aconno.sensorics.ui.devicecon

import java.util.*

data class ReadCommand(
    val serviceUUID: UUID,
    val charUUID: UUID
)