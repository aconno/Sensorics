package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Maybe

class FilterAdvertisementsUseCase(
    private val advertisementMatcher: AdvertisementMatcher
) : MaybeUseCaseWithParameter<ScanResult, ScanResult> {
    override fun execute(parameter: ScanResult): Maybe<ScanResult> {
        val advertisement: Advertisement = parameter.advertisement
        val countFormats: Int = advertisementMatcher.getCountOfMatchingFormats(advertisement)
        return if (countFormats == 1) {
            val scanResult = ScanResult(
                Device(
                    parameter.device.name,
                    parameter.device.macAddress,
                    advertisementMatcher.matchAdvertisementToFormat(advertisement).getIcon()
                ),
                advertisement
            )
            Maybe.just(scanResult)
        } else {
            Maybe.empty()
        }
    }
}