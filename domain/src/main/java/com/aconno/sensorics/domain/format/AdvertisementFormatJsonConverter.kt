package com.aconno.sensorics.domain.format

interface AdvertisementFormatJsonConverter {

    fun toAdvertisementFormat(jsonString: String): AdvertisementFormat
}