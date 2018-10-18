package com.aconno.sensorics.domain.interactor.format

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.repository.RemoteFormatRepository

class GetFormatsUseCase(
    private val remoteRemoteFormatRepository: RemoteFormatRepository
) {
    fun execute(): List<AdvertisementFormat> =
        remoteRemoteFormatRepository.getSupportedAdvertisementFormats()
}
