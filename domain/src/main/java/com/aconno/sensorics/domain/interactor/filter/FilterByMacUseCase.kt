package com.aconno.sensorics.domain.interactor.filter

import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Maybe

class FilterByMacUseCase
    : MaybeUseCaseWithTwoParameters<List<Reading>, List<Reading>, String> {

    override fun execute(
        firstParameter: List<Reading>,
        secondParameter: String
    ): Maybe<List<Reading>> {
        return if (firstParameter.isNotEmpty() &&
            firstParameter[0].device.macAddress == secondParameter) {
            Maybe.just(firstParameter)
        } else {
            Maybe.empty()
        }
    }
}