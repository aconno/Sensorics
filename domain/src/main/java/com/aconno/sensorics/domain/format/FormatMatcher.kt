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
        val advertisementMap = ByteOperations.isolateAdvertisementTypes(rawData)
        val advertisementLength = advertisementMap.map { it.value }.sumBy { it.size }

        return supportedFormats.filter { format ->
            advertisementLength >= format.getRequiredFormat().size // Eliminate advertisements that are too short
        }.firstOrNull { format ->
            checkAdvertisementFormatCompatibility(advertisementMap, format)
        } != null
    }

    fun findFormat(rawData: ByteArray): AdvertisementFormat? {
        val advertisementMap = ByteOperations.isolateAdvertisementTypes(rawData)
        val advertisementLength = advertisementMap.map { it.value }.sumBy { it.size }

        val matchedFormats = supportedFormats.filter { format ->
            advertisementLength >= format.getRequiredFormat().size // Eliminate advertisements that are too short
        }.filter { format ->
            checkAdvertisementFormatCompatibility(advertisementMap, format)
        }.toList()

        return when {
            matchedFormats.isEmpty() -> null
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

    private fun checkAdvertisementFormatCompatibility(
        advertisementMap: Map<Byte, ByteArray>,
        format: AdvertisementFormat
    ): Boolean {
        return format.getRequiredFormat().groupBy { it.source }.all { entry ->
            val type = entry.key
            val requiredBytes = entry.value
            advertisementMap[type]?.let { advertisementTypeData ->
                // Clear up settings support byte if we are looking at manufacturers data
                format.getSettingsSupport()?.takeIf {
                    type == 0xFF.toByte()
                }?.let { support ->
                    // Check if advertisement is even long enough to have a settings byte
                    advertisementTypeData.getOrNull(support.index)?.let { byte ->
                        advertisementTypeData[support.index] = byte and support.mask.inv()
                    }
                }

                requiredBytes.all { byteFormatRequired ->
                    advertisementTypeData.getOrNull(byteFormatRequired.position)
                        ?.let { actualByte ->
                            actualByte == byteFormatRequired.value
                        } ?: false // else return false because such byte does not exist
                }
            } ?: false // else return false because the required type of data is not provided
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