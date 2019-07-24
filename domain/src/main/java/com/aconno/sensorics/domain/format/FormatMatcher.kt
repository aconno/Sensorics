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
        return supportedFormats.any { format ->
            val advertisementMap = ByteOperations.isolateAdvertisementTypes(rawData)

            if (advertisementMap.map {
                    it.value
                }.sumBy {
                    it.size
                } >= format.getRequiredFormat().size) {

                format.getRequiredFormat().groupBy { requiredFormat ->
                    requiredFormat.source
                }.all { entry ->
                    val type = entry.key
                    val requiredBytes = entry.value
                    advertisementMap[type]?.let { data ->
                        matches(
                            data,
                            requiredBytes,
                            if (type == 0xFF.toByte()) format.getSettingsSupport() else null
                        )
                    } ?: false
                }
            } else {
                false
            }
        }
    }

    fun findFormat(rawData: ByteArray): AdvertisementFormat? {
        val matchedFormats = mutableListOf<AdvertisementFormat>()

        supportedFormats.forEach {format ->
            val advertisementMap = ByteOperations.isolateAdvertisementTypes(rawData)

            if (advertisementMap.map {
                    it.value
                }.sumBy {
                    it.size
                } < format.getRequiredFormat().size) {
                return@forEach
            }

            val matched = format.getRequiredFormat().groupBy { requiredFormat ->
                requiredFormat.source
            }.all { entry ->
                val type = entry.key
                val requiredBytes = entry.value
                advertisementMap[type]?.let { data ->
                    matches(
                        data,
                        requiredBytes,
                        if (type == 0xFF.toByte()) format.getSettingsSupport() else null
                    )
                } ?: false
            }

            if (matched) {
                matchedFormats.add(format)
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
        return bytes.let { data ->
            settingsSupport?.let { support ->
                data[support.index] = data[support.index] and support.mask.inv()
            }
            requiredBytes.all { format ->
                data[format.position] == format.value
            }
        }
    }

    fun getReadingTypes(formatName: String): List<String> {
        val readingTypes = mutableListOf<String>()
        supportedFormats.filter { it.getName() == formatName }
            .forEach {
                readingTypes.addAll(it.getFormat().keys)
            }
        return readingTypes.distinct().sorted()
    }
}