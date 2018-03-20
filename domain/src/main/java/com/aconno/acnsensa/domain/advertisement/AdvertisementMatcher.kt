package com.aconno.acnsensa.domain.advertisement

import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.model.Advertisement

/**
 * @author aconno
 */
class AdvertisementMatcher {
    private val supportedFormats: List<AdvertisementFormat> =
        listOf(ScalarsAdvertisementFormat(), VectorsAdvertisementFormat())

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
                format.getRequiredFormat(),
                format.getMaskBytePositions()
            )
            if (matches) {
                matchedFormats.add(format)
            }
        }
        return matchedFormats
    }

    private fun bytesMatchMask(bytes: List<Byte>, target: List<Byte>, mask: List<Int>): Boolean {
        if (bytes.size < target.size) {
            return false
        } else if (bytes.size >= target.size) {
            val inputBytesArePadded =
                bytes.filterIndexed { index, _ -> index >= target.size }.all { it.toInt() == 0x00 }
            if (inputBytesArePadded) {
                val unpadded: List<Byte> = bytes.filterIndexed { index, _ -> index < target.size }
                val output: List<Byte> = unpadded.mapIndexed { position, inputByte ->
                    if (mask.contains(position)) {
                        inputByte
                    } else {
                        0

                    }
                }

                return output == target
            }
        }

        return false
    }
}