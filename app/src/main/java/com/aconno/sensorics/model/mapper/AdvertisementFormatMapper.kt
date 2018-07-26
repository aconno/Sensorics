package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.ByteFormat
import com.aconno.sensorics.domain.format.ByteFormatRequired
import com.aconno.sensorics.domain.format.GenericFormat
import com.aconno.sensorics.model.ByteFormatModel
import com.aconno.sensorics.model.ByteFormatRequiredModel
import com.aconno.sensorics.model.GenericFormatModel
import com.aconno.sensorics.toHexByte

class AdvertisementFormatMapper {

    fun toAdvertisementFormat(genericFormatModel: GenericFormatModel): AdvertisementFormat {
        return GenericFormat(
            genericFormatModel.name,
            genericFormatModel.icon,
            genericFormatModel.format.map { toByteFormat(it) },
            genericFormatModel.formatRequired.map { toByteFormatRequired(it) }
        )
    }


    private fun toByteFormatRequired(byteFormatRequiredModel: ByteFormatRequiredModel): ByteFormatRequired {
        return ByteFormatRequired(
            byteFormatRequiredModel.name,
            byteFormatRequiredModel.index,
            byteFormatRequiredModel.value.toHexByte()
        )
    }

    private fun toByteFormat(byteFormatModel: ByteFormatModel): ByteFormat {
        return ByteFormat(
            byteFormatModel.name,
            byteFormatModel.startIndexInclusive,
            byteFormatModel.endIndexExclusive,
            byteFormatModel.reversed,
            byteFormatModel.dataType
        )
    }
}