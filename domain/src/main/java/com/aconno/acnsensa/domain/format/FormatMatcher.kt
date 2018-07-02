package com.aconno.acnsensa.domain.format

class FormatMatcher(
    private val supportedFormats: List<AdvertisementFormat>
) {

    fun matches(rawData: List<Byte>): Boolean {
        supportedFormats.forEach {
            if (matches(rawData, it.getRequiredFormat())) {
                return true
            }
        }
        return false
    }

    fun findFormat(rawData: List<Byte>): AdvertisementFormat? {
        supportedFormats.forEach {
            if (matches(rawData, it.getRequiredFormat())) {
                return it
            }
        }
        return null
    }

    private fun matches(bytes: List<Byte>, requiredBytes: List<ByteFormatRequired>): Boolean {
        requiredBytes.forEach {
            if (bytes[it.position] != it.value) {
                return false
            }
        }
        return true
    }
}