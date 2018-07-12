package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.model.ScanResult
import io.reactivex.Single

class GenerateScanDeviceUseCase(
    private val formatMatcher: FormatMatcher
) : SingleUseCaseWithParameter<ScanDevice, ScanResult> {

    override fun execute(parameter: ScanResult): Single<ScanDevice> {
        val format = formatMatcher.findFormat(parameter.rawData)
                ?: throw IllegalArgumentException("No format for scan result: $parameter")
        val device = Device(
            format.getName(),
            "",
            parameter.macAddress,
            format.getIcon()
        )
        val scanDevice = ScanDevice(
            device,
            parameter.rssi
        )
        return Single.just(scanDevice)
    }
}