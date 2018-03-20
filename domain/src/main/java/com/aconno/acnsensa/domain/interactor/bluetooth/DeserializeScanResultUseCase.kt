package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.advertisement.AdvertisementDeserializer
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.advertisement.ScalarsAdvertisementDeserializer
import com.aconno.acnsensa.domain.advertisement.VectorsAdvertisementDeserializer
import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single

/**
 * @author aconno
 */
class DeserializeScanResultUseCase(private val advertisementMatcher: AdvertisementMatcher) :
    SingleUseCaseWithParameter<Map<String, Number>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<Map<String, Number>> =
        Single.just(parameter).map { toSensorValues(it) }

    private fun toSensorValues(scanResult: ScanResult): Map<String, Number> {
        val advertisement: Advertisement = scanResult.advertisement

        val advertisementFormat: AdvertisementFormat =
            advertisementMatcher.matchAdvertisementToFormat(advertisement)
        val advertisementDeserializer: AdvertisementDeserializer =
            getAdvertisementDeserializer(advertisementFormat)

        return advertisementDeserializer.deserialize(advertisement)
    }

    private fun getAdvertisementDeserializer(advertisementFormat: AdvertisementFormat):
            AdvertisementDeserializer {
        return when (advertisementFormat) {
            is ScalarsAdvertisementFormat -> ScalarsAdvertisementDeserializer(advertisementFormat)
            is VectorsAdvertisementFormat -> VectorsAdvertisementDeserializer(advertisementFormat)
            else -> throw IllegalArgumentException("Invalid advertisement format")
        }
    }
}