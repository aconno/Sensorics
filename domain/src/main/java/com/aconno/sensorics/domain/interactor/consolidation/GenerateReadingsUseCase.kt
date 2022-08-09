package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.ByteOperations
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.isSettingsSupportOn
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import com.aconno.sensorics.domain.serialization.Deserializer
import io.reactivex.Single
import java.math.BigDecimal
import kotlin.experimental.and

class GenerateReadingsUseCase(
    private val formatMatcher: FormatMatcher,
    private val deserializer: Deserializer,
    private val deviceGroupDeviceJoinRepository : DeviceGroupDeviceJoinRepository,
    private val deviceGroupRepository : DeviceGroupRepository
) : SingleUseCaseWithParameter<List<Reading>, ScanResult> {

    override fun execute(parameter: ScanResult): Single<List<Reading>> {
        val sensorReadings = mutableListOf<Reading>()
        val msd = ByteOperations.isolateMsd(parameter.rawData)

        val deviceGroupDeviceRelations = deviceGroupDeviceJoinRepository.getAllDeviceGroupDeviceRelations().blockingGet()
        val deviceGroups = deviceGroupRepository.getAllDeviceGroups().blockingGet()

        val format = formatMatcher.findFormat(parameter.rawData)
            ?: throw IllegalArgumentException("No format for scan result: $parameter")
        format.getFormat().forEach {
            val name = it.key
            val byteFormat = it.value
            val device = generateDevice(format, parameter)
            val deviceGroup = deviceGroups.find { g -> deviceGroupDeviceRelations.find { r -> r.deviceId == device.macAddress  }?.deviceGroupId == g.id }

            evaluateFormula(byteFormat, msd)?.let { number ->
                val reading = Reading(
                    parameter.timestamp,
                    device,
                    number,
                    name,
                    parameter.rssi,
                    format.id,
                    deviceGroup
                )


                sensorReadings.add(reading)
            }
        }
        return Single.just(sensorReadings)
    }

    private fun evaluateFormula(byteFormat: ByteFormat, msd: ByteArray): Number? {
        try {
            val deserializedNumber = deserializer.deserializeNumber(
                msd,
                byteFormat
            )

            //If there is no formula,Don't do anything.
            return if (byteFormat.formula == null) {
                deserializedNumber
            } else {
                try {
                    //Evaluete expression
                    byteFormat.formula.with(
                        "x", BigDecimal(
                            deserializedNumber.toString()
                        )
                    ).eval()
                } catch (ex: Exception) {
                    //TODO Known Bug
                    //Attempt to read from field 'java.util.TreeMap$TreeMapEntry java.util.TreeMap$TreeMapEntry.left' on a null object reference
                    deserializedNumber
                }
            }

        } catch (ex: Exception) {
            return null
        }
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
                format.getConnectionReadList(),
                parameter.isSettingsSupportOn(format)
            )
        } else {
            device = Device(
                format.getName(),
                "",
                parameter.macAddress,
                format.getIcon(),
                hasSettings = parameter.isSettingsSupportOn(format)
            )
        }
        return device
    }

}