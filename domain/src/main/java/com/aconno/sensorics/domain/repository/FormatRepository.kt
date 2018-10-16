package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.AdvertisementFormat
import io.reactivex.Completable
import io.reactivex.Single

interface FormatRepository {

    fun getAllFormats(): Single<List<AdvertisementFormat>>

    fun getAllFormatIds(): Single<List<String>>

    fun getLastUpdateTimestamp(formatId: String): Single<Long>

    fun addOrReplaceFormat(formatId: String, formatJson: String): Completable
}