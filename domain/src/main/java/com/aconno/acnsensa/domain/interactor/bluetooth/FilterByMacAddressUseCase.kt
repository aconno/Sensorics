package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Maybe

class FilterByMacAddressUseCase : MaybeUseCaseWithTwoParameters<ScanResult, ScanResult, String> {

    override fun execute(scanResult: ScanResult, macAddress: String): Maybe<ScanResult> {
        return if (scanResult.device.macAddress == macAddress) {
            Maybe.just(scanResult)
        } else {
            Maybe.empty()
        }
    }
}