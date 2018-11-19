package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.AdvertisementFormat

interface FormatRepository {

    fun addFormat()
    fun getFormats(): List<AdvertisementFormat>
}