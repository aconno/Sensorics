package com.aconno.sensorics

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel

fun Device.getRealName(): String {
    return if (alias.isBlank()) name else alias
}

fun DeviceRelationModel.getRealName(): String {
    return if (alias.isBlank()) name else alias
}