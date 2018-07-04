package com.aconno.acnsensa.domain.interactor.consolidation

import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ScanResult
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
            if (name.startsWith("Magnetometer")) {
                val reading = Reading(
                    parameter.timestamp,
                    device,
                    deserializer.deserializeNumber(
                        parameter.rawData,
                        byteFormat
                    ).toFloat() * 0.00014,
                    name
                )
                sensorReadings.add(reading)
            } else if (name.startsWith("Accelerometer")) {
                val scaleFactorFormat = format.getFormat()["Accelerometer Scale Factor"]
                scaleFactorFormat?.let {
                    val reading = Reading(
                        parameter.timestamp,
                        device,
                        deserializer.deserializeNumber(
                            parameter.rawData,
                            byteFormat
                        ).toFloat() * deserializer.deserializeNumber(
                            parameter.rawData,
                            scaleFactorFormat
                        ).toFloat() / 65536,
                        name
                    )
                    sensorReadings.add(reading)
                }
            } else if (name.startsWith("Gyroscope")) {
                val reading = Reading(
                    parameter.timestamp,
                    device,
                    deserializer.deserializeNumber(
                        parameter.rawData,
                        byteFormat
                    ).toFloat() * 245 / 32768,
                    name
                )
                sensorReadings.add(reading)
            } else {
                val reading = Reading(
                    parameter.timestamp,
                    device,
                    deserializer.deserializeNumber(parameter.rawData, byteFormat),
                    name
                )
                sensorReadings.add(reading)
            }
        }
        return Single.just(sensorReadings)
    }
}