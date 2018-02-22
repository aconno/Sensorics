package com.aconno.acnsensa.domain.format

/**
 * @author aconno
 */
abstract class AcnSensaBaseFormat : AdvertisementFormat {
    override fun getFormat(): Map<String, ByteFormat> {
        val flagsLength = Pair(
            FLAGS_LENGTH,
            ByteFormat(
                startIndexInclusive = 0,
                endIndexExclusive = 1,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val flagsType = Pair(
            FLAGS_TYPE,
            ByteFormat(
                startIndexInclusive = 1,
                endIndexExclusive = 2,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val flags = Pair(
            FLAGS,
            ByteFormat(
                startIndexInclusive = 2,
                endIndexExclusive = 3,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val contentLength = Pair(
            CONTENT_LENGTH,
            ByteFormat(
                startIndexInclusive = 3,
                endIndexExclusive = 4,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val contentType = Pair(
            CONTENT_TYPE,
            ByteFormat(
                startIndexInclusive = 4,
                endIndexExclusive = 5,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )
        val vendorId = Pair(
            VENDOR_ID,
            ByteFormat(
                startIndexInclusive = 5,
                endIndexExclusive = 7,
                isReversed = false,
                targetType = SupportedTypes.SHORT
            )
        )
        val appId = Pair(
            APP_ID,
            ByteFormat(
                startIndexInclusive = 7,
                endIndexExclusive = 9,
                isReversed = false,
                targetType = SupportedTypes.SHORT
            )
        )
        val advertisementType = Pair(
            ADVERTISEMENT_TYPE,
            ByteFormat(
                startIndexInclusive = 9,
                endIndexExclusive = 10,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )

        return mapOf(
            flagsLength,
            flagsType,
            flags,
            contentLength,
            contentType,
            vendorId,
            appId,
            advertisementType
        )
    }

    protected val acnSensaMaskBytesPosition: List<Int> = (0..9).toList()
    protected fun generateAcnSensaRequiredFormat(type: Byte) = listOf(
        0x02,
        0x01,
        0x04,
        0x1A,
        0xFF.toByte(),
        0x59,
        0x00,
        0x17,
        0xCF.toByte(),
        type
    ) + List(20) { 0x00.toByte() }

    companion object {
        const val ADVERTISEMENT_TYPE = "Advertisement Type"
        const val FLAGS_LENGTH = "Flags Length"
        const val FLAGS_TYPE = "Flags Type"
        const val FLAGS = "FLags"
        const val CONTENT_LENGTH = "Content Length"
        const val CONTENT_TYPE = "Content Type"
        const val VENDOR_ID = "Vendor Id"
        const val APP_ID = "App Id"
    }
}