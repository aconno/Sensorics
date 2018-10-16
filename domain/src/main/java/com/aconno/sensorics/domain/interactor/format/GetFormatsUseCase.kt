package com.aconno.sensorics.domain.interactor.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository

class GetFormatsUseCase(
    private val remoteAdvertisementFormatRepository: AdvertisementFormatRepository
) {
    fun execute(): List<AdvertisementFormat> =
        remoteAdvertisementFormatRepository.getSupportedAdvertisementFormats()
}
