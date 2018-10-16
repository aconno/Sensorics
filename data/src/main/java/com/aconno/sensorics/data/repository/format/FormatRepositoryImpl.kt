package com.aconno.sensorics.data.repository.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.repository.FormatRepository

class FormatRepositoryImpl(
    private val formatDao: FormatDao,
    private val advertisementFormatJsonConverter: AdvertisementFormatJsonConverter
) : FormatRepository {

    override fun getAllFormats(): List<AdvertisementFormat> {
        return formatDao.getAllFormats().map { jsonString ->
            advertisementFormatJsonConverter.toAdvertisementFormat(jsonString)
        }
    }

    override fun getAllFormatIds(): List<String> {
        return formatDao.getAllFormatIds()
    }

    override fun getLastUpdateTimestamp(formatId: String): Long {
        return formatDao.getTimestamp(formatId)
    }

    override fun addOrReplaceFormat(
        formatId: String,
        timestamp: Long,
        formatJson: String
    ) {
        val formatEntity = FormatEntity(
            id = formatId,
            timestamp = timestamp,
            contentJson = formatJson
        )
        formatDao.insertOrReplace(formatEntity)
    }

    override fun deleteFormat(formatId: String) {
        formatDao.delete(formatId)
    }
}