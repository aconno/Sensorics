package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.interactor.format.GetFormatsUseCase

class FormatMatcher(
    private val getFormatsUseCase: GetFormatsUseCase
) {
    private val supportedFormats
        get() = getFormatsUseCase.execute()

    fun matches(rawData: List<Byte>): Boolean {
        supportedFormats.forEach {
            if (matches(isolateMsd(rawData), it.getRequiredFormat())) {
                return true
            }
        }
        return false
    }

    fun findFormat(rawData: List<Byte>): AdvertisementFormat? {
        supportedFormats.forEach {
            if (matches(isolateMsd(rawData), it.getRequiredFormat())) {
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

    private fun isolateMsd(rawData: List<Byte>): List<Byte> {
        var length: Byte = 0
        var type: Byte? = null
        rawData.forEachIndexed { i, byte ->
            if(length == 0x00.toByte()) {
                length = byte
                type = null
            } else {
                if(type == null) type = byte
                else {
                    if(type==0xFF.toByte()) return rawData.toByteArray().copyOfRange(i, i+length).toList()
                }
                length--
            }
        }
        return rawData
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