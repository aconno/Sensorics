package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.ByteOperations
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase

class FormatMatcher(
    private val getFormatsUseCase: GetFormatsUseCase
) {
    private val supportedFormats
        get() = getFormatsUseCase.execute()

    fun matches(rawData: ByteArray): Boolean {
        supportedFormats.forEach {
            if (matches(ByteOperations.isolateMsd(rawData), it.getRequiredFormat())) {
                return true
            }
        }
        return false
    }

    fun findFormat(rawData: ByteArray): AdvertisementFormat? {
        supportedFormats.forEach {
            if (matches(ByteOperations.isolateMsd(rawData), it.getRequiredFormat())) {
                return it
            }
        }
        return null
    }

    private fun matches(bytes: ByteArray, requiredBytes: List<ByteFormatRequired>): Boolean {
        requiredBytes.forEach {
            if (bytes[it.position] != it.value) {
                return false
            }
        }
        return true
    }

    fun getReadingTypes(formatName: String): List<String> {
        val readingTypes = mutableListOf<String>()
        supportedFormats.filter { it.getName() == formatName }
            .forEach {
                readingTypes.addAll(it.getFormat().keys)
            }
        return readingTypes.sorted()
    }
}