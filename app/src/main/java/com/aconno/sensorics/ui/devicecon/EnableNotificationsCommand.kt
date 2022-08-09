package com.aconno.sensorics.ui.devicecon

import java.util.*

data class EnableNotificationsCommand(
    val serviceUUID: UUID,
    val charUUID: UUID,
    val isEnabled: Boolean
)