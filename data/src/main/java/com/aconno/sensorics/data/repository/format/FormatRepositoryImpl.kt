package com.aconno.sensorics.data.repository.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.AdvertisementFormatJsonConverter
import com.aconno.sensorics.domain.repository.FormatRepository
import io.reactivex.Completable
import io.reactivex.Single

class FormatRepositoryImpl(
    private val formatDao: FormatDao,
    private val advertisementFormatJsonConverter: AdvertisementFormatJsonConverter
) : FormatRepository {

    override fun getAllFormats(): Single<List<AdvertisementFormat>> {
        return formatDao.getAllFormats().map { jsonStrings ->
            jsonStrings.map { jsonString ->
                advertisementFormatJsonConverter.toAdvertisementFormat(jsonString)
            }
        }
    }

    override fun getAllFormatIds(): Single<List<String>> {
        return formatDao.getAllFormatIds()
    }

    override fun getLastUpdateTimestamp(formatId: String): Single<Long> {
        return formatDao.getTimestamp(formatId)
    }

    override fun addOrReplaceFormat(
        formatId: String,
        timestamp: Long,
        formatJson: String
    ): Completable {
        return Completable.fromAction {
            val formatEntity = FormatEntity(
                id = formatId,
                timestamp = timestamp,
                contentJson = formatJson
            )
            formatDao.insertOrReplace(formatEntity)
        }
    }

    override fun deleteFormat(formatId: String): Completable {
        return Completable.fromAction {
            formatDao.delete(formatId)
        }
    }
}