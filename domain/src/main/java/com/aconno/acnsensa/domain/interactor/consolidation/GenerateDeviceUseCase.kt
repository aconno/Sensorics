package com.aconno.acnsensa.domain.interactor.consolidation

import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
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