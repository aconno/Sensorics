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

    override fun getLastUpdateTimestamp(formatId: String): Single<Long> {
        return formatDao.getTimestamp(formatId)
    }

    override fun addOrReplaceFormat(formatId: String, formatJson: String): Completable {
        return Completable.fromAction {
            val formatEntity = FormatEntity(
                id = formatId,
                timestamp = System.currentTimeMillis(), //TODO: fix this
                contentJson = formatJson
            )
            formatDao.insertOrReplace(formatEntity)
        }
    }
}