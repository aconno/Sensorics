package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.AdvertisementFormat
import io.reactivex.Single

interface FormatRepository {

    fun getAllFormats(): Single<List<AdvertisementFormat>>
}