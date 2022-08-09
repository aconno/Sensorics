package com.aconno.sensorics.domain.interactor.filter

import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.model.ScanResult

class FilterByFormatUseCase(private val formatMatcher: FormatMatcher) {

    fun execute(parameter: ScanResult) = formatMatcher.matches(parameter.rawData)
}