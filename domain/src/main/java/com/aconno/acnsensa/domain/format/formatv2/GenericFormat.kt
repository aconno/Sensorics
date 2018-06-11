package com.aconno.acnsensa.domain.format.formatv2

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ByteFormat

data class GenericFormat(
    private val formatName: String,
    private val icon: String,
    private val format: List<Format>,
    private val requiredFormat: List<RequiredFormat>
) : AdvertisementFormat {

    override fun getFormat(): Map<String, ByteFormat> {
        val map = hashMapOf<String, ByteFormat>()
        format.forEach {
            map[it.propertyName] = ByteFormat(
                it.startInclusive,
                it.endExclusive,
                it.reversed,
                it.type
            )
        }

        return map
    }

    override fun getRequiredFormat(): List<Byte> {



    }

    override fun getMaskBytePositions(): List<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}