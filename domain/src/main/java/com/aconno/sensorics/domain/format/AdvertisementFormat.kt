package com.aconno.sensorics.domain.format

interface AdvertisementFormat : Connection {

    fun getIcon(): String
    fun getFormat(): Map<String, ByteFormat>
    fun getRequiredFormat(): List<ByteFormatRequired>
}