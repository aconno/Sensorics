package com.aconno.sensorics.model

import com.aconno.sensorics.domain.model.Device

data class DeviceActive(
    private val startedDevice: Device,
    var active: Boolean
) {

    var device = startedDevice
        private set

    fun updateDevice(updatedDevice: Device) {
        if (device != updatedDevice) {
            throw IllegalArgumentException("Can't update device $device because parameter $updatedDevice is not equals to it")
        }
        device = updatedDevice
    }

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