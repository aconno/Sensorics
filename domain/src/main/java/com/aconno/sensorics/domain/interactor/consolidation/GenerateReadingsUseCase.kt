package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.ByteOperations
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.serialization.Deserializer
import io.reactivex.Single

class GenerateReadingsUseCase(
    private val formatMatcher: FormatMatcher,
    private val deserializer: Deserializer
) : SingleUseCaseWithParameter<List<Reading>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<List<Reading>> {
        val sensorReadings = mutableListOf<Reading>()
        val msd = ByteOperations.isolateMsd(parameter.rawData)
        val format = formatMatcher.findFormat(parameter.rawData)
            ?: throw IllegalArgumentException("No format for scan result: $parameter")
        format.getFormat().forEach {
            val name = it.key
            val byteFormat = it.value
            val device = generateDevice(format, parameter)
            when {
                name.startsWith("Magnetometer") -> {
                    val reading = Reading(
                        parameter.timestamp,
                        device,
                        deserializer.deserializeNumber(
                            msd,
                            byteFormat
                        ).toFloat() * 0.00014,
                        name,
                        format.id
                    )
                    sensorReadings.add(reading)
                }
                name.startsWith("Accelerometer") -> {
                    val scaleFactorFormat = format.getFormat()["Accelerometer Scale Factor"]
                    scaleFactorFormat?.let {
                        val reading = Reading(
                            parameter.timestamp,
                            device,
                            deserializer.deserializeNumber(
                                msd,
                                byteFormat
                            ).toFloat() * deserializer.deserializeNumber(
                                msd,
                                scaleFactorFormat
                            ).toFloat() / 65536,
                            name,
                            format.id
                        )
                        sensorReadings.add(reading)
                    }
                }
                name.startsWith("Gyroscope") -> {
                    val reading = Reading(
                        parameter.timestamp,
                        device,
                        deserializer.deserializeNumber(
                            msd,
                            byteFormat
                        ).toFloat() * 245 / 32768,
                        name,
                        format.id
                    )
                    sensorReadings.add(reading)
                }
                else -> {
                    val reading = Reading(
                        parameter.timestamp,
                        device,
                        deserializer.deserializeNumber(msd, byteFormat),
                        name,
                        format.id
                    )
                    sensorReadings.add(reading)
                }
            }
        }
        return Single.just(sensorReadings)
    }

    private fun generateDevice(
        format: AdvertisementFormat,
        parameter: ScanResult
    ): Device {
        val device: Device
        if (format.isConnectible()) {
            device = Device(
                format.getName(),
                "",
                parameter.macAddress,
                format.getIcon(),
                format.isConnectible(),
                format.getConnectionWriteList(),
                format.getConnectionReadList()
            )
        } else {
            device = Device(
                format.getName(),
                "",
                parameter.macAddress,
                format.getIcon()
            )
        }
        return device
    }
}