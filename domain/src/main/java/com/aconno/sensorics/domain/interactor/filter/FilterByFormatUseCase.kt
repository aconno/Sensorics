package com.aconno.sensorics.domain.interactor.filter

import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.model.ScanResult
import io.reactivex.Maybe

class FilterByFormatUseCase(
    private val formatMatcher: FormatMatcher
) : MaybeUseCaseWithParameter<ScanResult, ScanResult> {

    override fun execute(parameter: ScanResult): Maybe<ScanResult> {
        return if (formatMatcher.matches(parameter.rawData)) {
            Maybe.just(parameter)
        } else {
            Maybe.empty()
        }
    }
}