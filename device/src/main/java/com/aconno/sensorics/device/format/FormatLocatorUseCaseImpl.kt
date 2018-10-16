package com.aconno.sensorics.device.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.FormatLocatorUseCase
import com.aconno.sensorics.domain.repository.FormatRepository
import io.reactivex.Single

class FormatLocatorUseCaseImpl(
    private val formatRepository: FormatRepository
) : FormatLocatorUseCase {

    override fun execute(): Single<List<AdvertisementFormat>> {
        //TODO("not implemented")
        return Single.just(listOf())
    }
}