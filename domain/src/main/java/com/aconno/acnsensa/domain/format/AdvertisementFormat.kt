package com.aconno.acnsensa.domain.format

interface AdvertisementFormat {

    fun getName(): String
    fun getIcon(): String
    fun getFormat(): Map<String, ByteFormat>
    fun getRequiredFormat(): List<ByteFormatRequired>
}