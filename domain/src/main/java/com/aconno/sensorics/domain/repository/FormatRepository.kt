package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.AdvertisementFormat

interface FormatRepository {

    fun getAllFormats(): List<AdvertisementFormat>

    fun getAllFormatIds(): List<String>

    fun getLastUpdateTimestamp(formatId: String): Long

    fun addOrReplaceFormat(formatId: String, timestamp: Long, formatJson: String)

    fun deleteFormat(formatId: String)
}