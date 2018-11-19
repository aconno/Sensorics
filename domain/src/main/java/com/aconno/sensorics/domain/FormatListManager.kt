package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.repository.FormatRepository

class FormatListManager(
    private val formatRepository: FormatRepository
) {

    var isDirty: Boolean = true

    private var formatList: List<AdvertisementFormat> = listOf()

    fun getFormats(): List<AdvertisementFormat> {
        if (isDirty) {
            formatList = formatRepository.getFormats()
            isDirty = false
        }

        return formatList
    }
}