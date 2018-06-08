package com.aconno.acnsensa.domain.interactor.filter

import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.acnsensa.domain.model.SensorReading
import io.reactivex.Maybe

class FilterReadingsByMacAddressUseCase :
    MaybeUseCaseWithTwoParameters<List<SensorReading>, List<SensorReading>, String> {

    override fun execute(
        firstParameter: List<SensorReading>,
        secondParameter: String
    ): Maybe<List<SensorReading>> {
        return if (firstParameter[0].device.macAddress == secondParameter) {
            Maybe.just(firstParameter)
        } else {
            Maybe.empty()
        }
    }
}