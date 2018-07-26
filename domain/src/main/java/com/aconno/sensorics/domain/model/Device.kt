package com.aconno.sensorics.domain.model

import com.aconno.sensorics.domain.format.ConnectionRead
import com.aconno.sensorics.domain.format.ConnectionWrite

data class Device(
    val name: String,
    val alias: String,
    val macAddress: String,
    val icon: String = "",
    val connectable: Boolean = false,
    val connectionWriteList: List<ConnectionWrite>? = null,
    val connectionReadList: List<ConnectionRead>? = null
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