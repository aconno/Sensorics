package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.ByteOperations
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import kotlin.experimental.and
import kotlin.experimental.inv

class FormatMatcher(
    private val getFormatsUseCase: GetFormatsUseCase
) {
    private val supportedFormats
        get() = getFormatsUseCase.execute()

    fun matches(rawData: ByteArray): Boolean {
        supportedFormats.forEach {
            if (matches(
                    ByteOperations.isolateMsd(rawData),
                    it.getRequiredFormat(),
                    it.getSettingsSupport()
                )
            ) {
                return true
            }
        }
        return false
    }

    fun findFormat(rawData: ByteArray): AdvertisementFormat? {
        supportedFormats.forEach {
            if (matches(
                    ByteOperations.isolateMsd(rawData),
                    it.getRequiredFormat(),
                    it.getSettingsSupport()
                )
            ) {
                return it
            }
        }
        return null
    }

    private fun matches(
        bytes: ByteArray,
        requiredBytes: List<ByteFormatRequired>,
        settingsSupport: SettingsSupport?
    ): Boolean {
        requiredBytes.forEachIndexed { index, it ->
            if (index == settingsSupport?.index) {
                bytes[it.position] = bytes[it.position] and settingsSupport.mask.inv()
            }

            if (bytes[it.position] != it.value) {
                return false
            }
        }
        return true
    }

    private fun matches(bytes: ByteArray, requiredBytes: List<ByteFormatRequired>): Boolean {
        requiredBytes.forEachIndexed { index, it ->
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