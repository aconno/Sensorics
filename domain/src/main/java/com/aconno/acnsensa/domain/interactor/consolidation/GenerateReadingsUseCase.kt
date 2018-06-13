package com.aconno.acnsensa.domain.interactor.consolidation

import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.interactor.filter.ReadingType
import com.aconno.acnsensa.domain.interactor.filter.ScanResult
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.serialization.Deserializer
import io.reactivex.Single

class GenerateReadingsUseCase(
    private val formatMatcher: FormatMatcher,
    private val deserializer: Deserializer
) : SingleUseCaseWithParameter<List<Reading>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<List<Reading>> {
        val sensorReadings = mutableListOf<Reading>()
        val format = formatMatcher.findFormat(parameter.rawData)
                ?: throw IllegalArgumentException("No format for scan result: $parameter")
        format.getFormat().forEach { name, byteFormat ->
            val device = Device(
                format.getName(),
                parameter.macAddress,
                format.getIcon()
            )
            val reading = Reading(
                parameter.timestamp,
                device,
                deserializer.deserializeNumber(parameter.rawData, byteFormat),
                ReadingType.fromString(name)
            )
            sensorReadings.add(reading)
        }
        return Single.just(sensorReadings)
    }
}