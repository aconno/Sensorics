package com.aconno.sensorics.domain.interactor.format

import com.aconno.sensorics.domain.format.RemoteAdvertisementFormat
import com.aconno.sensorics.domain.repository.AdvertisementFormatRepository
import io.reactivex.Single

class GetRemoteFormatsUseCase(
        private val remoteAdvertisementFormatRepository: AdvertisementFormatRepository) {
    fun execute(): Single<List<RemoteAdvertisementFormat>> =
            remoteAdvertisementFormatRepository.getSupportedAdvertisementFormats()
}
