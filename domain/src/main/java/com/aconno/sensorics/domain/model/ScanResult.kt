package com.aconno.sensorics.domain.model

data class ScanResult(
    val timestamp: Long,
    val macAddress: String,
    val rssi: Int,
    val rawData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResult

        if (timestamp != other.timestamp) return false
        if (macAddress != other.macAddress) return false
        if (rssi != other.rssi) return false
        if (!rawData.contentEquals(other.rawData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + macAddress.hashCode()
        result = 31 * result + rssi
        result = 31 * result + rawData.contentHashCode()
        return result
    }
}