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
            val isolateMsd = ByteOperations.isolateMsd(rawData)

            if (isolateMsd.size < it.getRequiredFormat().size) {
                return@forEach
            }

            if (matches(
                    isolateMsd,
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
        val matchedFormats = mutableListOf<AdvertisementFormat>()

        supportedFormats.forEach {
            val isolateMsd = ByteOperations.isolateMsd(rawData)

            if (isolateMsd.size < it.getRequiredFormat().size) {
                return@forEach
            }

            if (matches(
                    isolateMsd,
                    it.getRequiredFormat(),
                    it.getSettingsSupport()
                )
            ) {
                matchedFormats.add(it)
            }
        }

        return when {
            matchedFormats.size == 0 -> null
            matchedFormats.size == 1 -> matchedFormats[0]
            else -> {
                var bestFormatForThisAdvertisement = matchedFormats[0]

                matchedFormats.forEach {
                    if (it.getRequiredFormat().size > bestFormatForThisAdvertisement.getRequiredFormat().size) {
                        bestFormatForThisAdvertisement = it
                    }
                }

                bestFormatForThisAdvertisement
            }
        }
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

    fun getReadingTypes(formatName: String): List<String> {
        val readingTypes = mutableListOf<String>()
        supportedFormats.filter { it.getName() == formatName }
            .forEach {
                readingTypes.addAll(it.getFormat().keys)
            }
        return readingTypes.sorted()
    }
}