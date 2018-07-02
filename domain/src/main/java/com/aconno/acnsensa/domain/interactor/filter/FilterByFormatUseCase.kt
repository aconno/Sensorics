package com.aconno.acnsensa.domain.interactor.filter

import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.model.ScanResult
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