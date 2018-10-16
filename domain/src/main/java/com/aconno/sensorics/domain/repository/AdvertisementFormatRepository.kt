package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.RemoteAdvertisementFormat
import io.reactivex.Single

interface AdvertisementFormatRepository {
    fun getSupportedAdvertisementFormats(): Single<List<RemoteAdvertisementFormat>>
}
