package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.serialization.Deserializer
import com.udojava.evalex.Expression
import io.reactivex.Single
import java.math.BigDecimal

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

            val reading = Reading(
                parameter.timestamp,
                device,
                evaluateFormula(byteFormat, msd),
                name,
                format.id
            )
            sensorReadings.add(reading)
        }
        return Single.just(sensorReadings)
    }

    private fun evaluateFormula(byteFormat: ByteFormat, msd: List<Byte>): Number {
        val deserializedNumber = deserializer.deserializeNumber(
            msd,
            byteFormat
        )

        //If there is no formula,Don't do anything.
        return if (byteFormat.formula == null || byteFormat.formula!!.isBlank()) {
            deserializedNumber
        } else {
            //Evaluete expression
            Expression(byteFormat.formula).with(
                "x", BigDecimal(
                    deserializedNumber.toString()
                )
            ).eval()
        }
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