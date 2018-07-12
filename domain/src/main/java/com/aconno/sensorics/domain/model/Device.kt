package com.aconno.sensorics.domain.model

data class Device(
    val name: String,
    val alias: String,
    val macAddress: String,
    val icon: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Device
        return macAddress == other.macAddress
    }

    override fun hashCode(): Int {
        return macAddress.hashCode()
    }
}