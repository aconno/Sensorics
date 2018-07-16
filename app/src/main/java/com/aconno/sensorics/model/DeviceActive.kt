package com.aconno.sensorics.model

import com.aconno.sensorics.domain.model.Device

data class DeviceActive(
    val device: Device,
    var active: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as DeviceActive
        return device == other.device
    }

    override fun hashCode(): Int {
        return device.hashCode()
    }
}