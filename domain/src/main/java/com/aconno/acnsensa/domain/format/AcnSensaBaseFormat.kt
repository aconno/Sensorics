package com.aconno.acnsensa.domain.format

abstract class AcnSensaBaseFormat : AdvertisementFormat {

    override fun getFormat(): Map<String, ByteFormat> {
        val flagsLength = Pair(
            FLAGS_LENGTH, ByteFormat(
                name = FLAGS_LENGTH,
                startIndexInclusive = 0,
                endIndexExclusive = 1,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )

        val flagsType = Pair(
            FLAGS_TYPE, ByteFormat(
                name = FLAGS_TYPE,
                startIndexInclusive = 1,
                endIndexExclusive = 2,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )
        val flags = Pair(
            FLAGS, ByteFormat(
                name = FLAGS,
                startIndexInclusive = 2,
                endIndexExclusive = 3,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )
        val contentLength = Pair(
            CONTENT_LENGTH, ByteFormat(
                name = CONTENT_LENGTH,
                startIndexInclusive = 3,
                endIndexExclusive = 4,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )
        val contentType = Pair(
            CONTENT_TYPE, ByteFormat(
                name = CONTENT_TYPE,
                startIndexInclusive = 4,
                endIndexExclusive = 5,
                isReversed = false,
                dataType = SupportedTypes.BYTE
            )
        )
        val vendorId = Pair(
            VENDOR_ID, ByteFormat(
                name = VENDOR_ID,
                startIndexInclusive = 5,
                endIndexExclusive = 7,
                isReversed = false,
                dataType = SupportedTypes.SHORT
            )
        )
        val appId = Pair(
            APP_ID, ByteFormat(
                name = APP_ID,
                startIndexInclusive = 7,
                endIndexExclusive = 9,
                isReversed = false,
                dataType = SupportedTypes.SHORT
            )
        )
        val advertisementType = Pair(
            ADVERTISEMENT_TYPE, ByteFormat(
                name = ADVERTISEMENT_TYPE,
                startIndexInclusive = 9,
                endIndexExclusive = 10,
                isReversed = false,
                dataType = SupportedTypes.BYTE
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

    protected fun generateAcnSensaRequiredFormat(advertisementType: Byte) = listOf(
        ByteFormatRequired(
            name = FLAGS_LENGTH,
            value = 0x02,
            position = 0
        ),
        ByteFormatRequired(
            name = FLAGS_TYPE,
            value = 0x01,
            position = 1
        ),
        ByteFormatRequired(
            name = FLAGS,
            value = 0x04,
            position = 2
        ),
        ByteFormatRequired(
            name = CONTENT_LENGTH,
            value = 0x1A,
            position = 3
        ),
        ByteFormatRequired(
            name = CONTENT_TYPE,
            value = 0xFF.toByte(),
            position = 4
        ),
        ByteFormatRequired(
            name = "$VENDOR_ID 1",
            value = 0x59,
            position = 5
        ),
        ByteFormatRequired(
            name = "$VENDOR_ID 2",
            value = 0x00,
            position = 6
        ),
        ByteFormatRequired(
            name = "$APP_ID 1",
            value = 0x17,
            position = 7
        ),
        ByteFormatRequired(
            name = "$APP_ID 2",
            value = 0xCF.toByte(),
            position = 8
        ),
        ByteFormatRequired(
            name = ADVERTISEMENT_TYPE,
            value = advertisementType,
            position = 9
        )
    )

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