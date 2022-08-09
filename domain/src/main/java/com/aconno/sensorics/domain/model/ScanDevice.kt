package com.aconno.sensorics.domain.model

data class ScanDevice(
    val device: Device,
    val rssi: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as ScanDevice
        return device.macAddress == other.device.macAddress
    }

    override fun hashCode(): Int {
        return device.macAddress.hashCode()
    }
}