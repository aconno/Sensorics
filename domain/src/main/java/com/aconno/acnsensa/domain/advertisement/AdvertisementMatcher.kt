package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ByteFormatRequired
import com.aconno.acnsensa.domain.model.Advertisement

class AdvertisementMatcher(
    private val supportedFormats: List<AdvertisementFormat>
) {

    fun getCountOfMatchingFormats(advertisement: Advertisement): Int {
        val matchedFormats: List<AdvertisementFormat> = getMatchedFormats(advertisement)
        return matchedFormats.size
    }

    fun matchAdvertisementToFormat(advertisement: Advertisement): AdvertisementFormat {
        val matchedFormats: List<AdvertisementFormat> = getMatchedFormats(advertisement)

        if (matchedFormats.size == 1) {
            return matchedFormats[0]
        }

        throw IllegalArgumentException(
            "Advertisement must match only 1 format. " +
                    "Matched formats = ${matchedFormats.size}"
        )
    }

    private fun getMatchedFormats(advertisement: Advertisement): List<AdvertisementFormat> {
        val matchedFormats: MutableList<AdvertisementFormat> = ArrayList()
        for (format in supportedFormats) {
            val matches: Boolean = bytesMatchMask(
                advertisement.rawData,
                format.getRequiredFormat()
            )
            if (matches) {
                matchedFormats.add(format)
            }
        }
        return matchedFormats
    }

    private fun bytesMatchMask(bytes: List<Byte>, target: List<ByteFormatRequired>): Boolean {
        target.forEach {
            if (bytes[it.position] != it.value) {
                return false
            }
        }
        return true
    }
}