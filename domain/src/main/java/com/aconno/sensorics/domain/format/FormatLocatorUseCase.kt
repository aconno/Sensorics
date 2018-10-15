package com.aconno.sensorics.domain.format

import io.reactivex.Single

interface FormatLocatorUseCase {

    fun execute(): Single<List<AdvertisementFormat>>
}