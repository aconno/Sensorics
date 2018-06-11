package com.aconno.acnsensa.domain.format

interface AdvertisementFormat {
    fun getFormat(): Map<String, ByteFormat>
    fun getRequiredFormat(): List<ByteFormatRequired>
}