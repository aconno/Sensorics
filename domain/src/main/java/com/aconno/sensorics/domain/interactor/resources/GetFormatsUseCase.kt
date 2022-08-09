package com.aconno.sensorics.domain.interactor.resources

import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.format.AdvertisementFormat

class GetFormatsUseCase(
    private val formatListManager: FormatListManager
) {
    fun execute(): List<AdvertisementFormat> =
        formatListManager.getFormats()
}
