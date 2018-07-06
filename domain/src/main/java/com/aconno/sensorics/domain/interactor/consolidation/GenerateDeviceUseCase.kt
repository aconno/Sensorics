package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Single

class GenerateDeviceUseCase(
    private val formatMatcher: FormatMatcher
) : SingleUseCaseWithParameter<Device, ScanResult> {

    override fun execute(parameter: ScanResult): Single<Device> {
        val format = formatMatcher.findFormat(parameter.rawData)
                ?: throw IllegalArgumentException("No format for scan result: $parameter")
        val device = Device(
            format.getName(),
            parameter.macAddress,
            format.getIcon()
        )
        return Single.just(device)
    }
}