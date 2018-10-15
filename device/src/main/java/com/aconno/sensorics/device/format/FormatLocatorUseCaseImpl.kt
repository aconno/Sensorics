package com.aconno.sensorics.device.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.FormatLocatorUseCase
import io.reactivex.Single

class FormatLocatorUseCaseImpl : FormatLocatorUseCase {

    override fun execute(): Single<List<AdvertisementFormat>> {
        //TODO("not implemented")
        return Single.just(listOf())
    }
}