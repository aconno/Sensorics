package com.aconno.acnsensa.domain.format

/**
 * @author aconno
 */
interface AdvertisementFormat {
    fun getFormat(): Map<String, ByteFormat>
    fun getRequiredFormat(): List<Byte>
    fun getMaskBytePositions(): List<Int>
}