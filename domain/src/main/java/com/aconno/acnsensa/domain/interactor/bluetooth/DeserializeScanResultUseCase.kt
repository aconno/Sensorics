package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.serialization.Deserializer
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single

class DeserializeScanResultUseCase(
    private val advertisementMatcher: AdvertisementMatcher,
    private val deserializer: Deserializer
) : SingleUseCaseWithParameter<Map<String, Number>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<Map<String, Number>> =
        Single.just(parameter).map { toSensorValues(it) }

    private fun toSensorValues(scanResult: ScanResult): Map<String, Number> {
        val map = mutableMapOf<String, Number>()
        val rawData = scanResult.advertisement.rawData
        val advertisementFormat =
            advertisementMatcher.matchAdvertisementToFormat(scanResult.advertisement)
        advertisementFormat.getFormat().forEach { name, byteFormat ->
            map.put(name, deserializer.deserializeNumber(rawData, byteFormat))
        }
        return map
    }
}