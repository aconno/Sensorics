package com.aconno.sensorics.data.repository.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.repository.FormatRepository
import io.reactivex.Single

class FormatRepositoryImpl(
    formatDao: FormatDao
) : FormatRepository {

    override fun getAllFormats(): Single<List<AdvertisementFormat>> {
        //TODO("not implemented")
        return Single.just(listOf())
    }
}