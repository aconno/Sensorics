package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.format.AdvertisementFormat
import io.reactivex.Completable

interface AdvertisementFormatRepository {
    fun getSupportedAdvertisementFormats(): List<AdvertisementFormat>
    fun updateAdvertisementFormats(): Completable
}
