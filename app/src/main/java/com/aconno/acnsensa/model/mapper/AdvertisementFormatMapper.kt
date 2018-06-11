package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ByteFormat
import com.aconno.acnsensa.domain.format.ByteFormatRequired
import com.aconno.acnsensa.domain.format.GenericFormat
import com.aconno.acnsensa.model.ByteFormatModel
import com.aconno.acnsensa.model.ByteFormatRequiredModel
import com.aconno.acnsensa.model.GenericFormatModel

class AdvertisementFormatMapper {

    fun toAdvertisementModel(genericFormatModel: GenericFormatModel): AdvertisementFormat {
        return GenericFormat(
            genericFormatModel.formatName,
            genericFormatModel.icon,
            genericFormatModel.format.map { toByteFormat(it) },
            genericFormatModel.requiredFormat.map { toByteFormatRequired(it) }
        )
    }


    private fun toByteFormatRequired(byteFormatRequiredModel: ByteFormatRequiredModel): ByteFormatRequired {
        return ByteFormatRequired(
            byteFormatRequiredModel.name,
            byteFormatRequiredModel.position,
            byteFormatRequiredModel.value
        )
    }

    private fun toByteFormat(byteFormatModel: ByteFormatModel): ByteFormat {
        return ByteFormat(
            byteFormatModel.name,
            byteFormatModel.startIndexInclusive,
            byteFormatModel.endIndexExclusive,
            byteFormatModel.isReversed,
            byteFormatModel.dataType
        )
    }
}