package com.aconno.sensorics.domain.interactor.consolidation

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
        val msd = isolateMsd(parameter.rawData)
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
                        name
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
                            name
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
                        name
                    )
                    sensorReadings.add(reading)
                }
                else -> {
                    val reading = Reading(
                        parameter.timestamp,
                        device,
                        deserializer.deserializeNumber(msd, byteFormat),
                        name
                    )
                    sensorReadings.add(reading)
                }
            }
        }
        return Single.just(sensorReadings)
    }

    private fun isolateMsd(rawData: List<Byte>): List<Byte> {
        var length: Byte = 0
        var type: Byte? = null
        rawData.forEachIndexed { i, byte ->
            if (length == 0x00.toByte()) {
                length = byte
                type = null
            } else {
                if (type == null) type = byte
                else {
                    if (type == 0xFF.toByte()) return rawData.toByteArray()
                        .copyOfRange(i, i + length).toList()
                }
                length--
            }
        }
        return rawData
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