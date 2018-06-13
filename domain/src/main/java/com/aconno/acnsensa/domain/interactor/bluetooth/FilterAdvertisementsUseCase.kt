package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Maybe

class FilterAdvertisementsUseCase(
    private val formatMatcher: FormatMatcher
) : MaybeUseCaseWithParameter<ScanResult, ScanResult> {
    override fun execute(parameter: ScanResult): Maybe<ScanResult> {
        val advertisement: Advertisement = parameter.advertisement
        return if (formatMatcher.matches(advertisement.rawData)) {
            val advertisementFormat = formatMatcher.findFormat(advertisement.rawData)
                    ?: throw IllegalArgumentException("No format for scan result: $parameter")
            val scanResult = ScanResult(
                Device(
                    advertisementFormat.getName(),
                    parameter.device.macAddress,
                    advertisementFormat.getIcon()
                ),
                advertisement
            )
            Maybe.just(scanResult)
        } else {
            Maybe.empty()
        }
    }
}