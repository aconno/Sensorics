package com.aconno.sensorics

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel

fun Device.getRealName(): String {
    return if (alias.isBlank()) name else alias
}

fun DeviceRelationModel.getRealName(): String {
    return if (alias.isBlank()) name else alias
}

fun String.toHexByte(): Byte {
    return (Integer.parseInt(
        this.replace("0x", ""),
        16
    ) and 0xff).toByte()
}

fun List<Byte>.toHexString(): String {
    return joinToString(separator = " ") { String.format("%02X", it) }
}