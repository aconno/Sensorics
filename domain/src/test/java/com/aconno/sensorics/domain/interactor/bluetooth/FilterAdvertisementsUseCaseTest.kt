package com.aconno.sensorics.domain.interactor.bluetooth

import com.aconno.sensorics.domain.Util
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.ByteFormatRequired
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.model.ScanResult
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class FilterAdvertisementsUseCaseTest {

    @Test
    fun filterByMatchingFormat() {
        val mockAdvertisementFormat = mockAdvertisementFormat(
            mockRequiredFormat(Util.REQUIRED_FORMAT_BYTES)
        )
        val formatMatcher = FormatMatcher(mockGetFormatsUseCase(mockAdvertisementFormat))

        val filterByFormatUseCase = FilterByFormatUseCase(formatMatcher)
        val testScanResult =
            getTestScanResult("D9:D9:D9:D9:D9:D9", Util.BEACON_BYTES)

        assertTrue(filterByFormatUseCase.execute(testScanResult))
    }

    @Test
    fun filterByNotMatchingFormat() {
        val mockAdvertisementFormat = mockAdvertisementFormat(
            mockRequiredFormat(Util.REQUIRED_FORMAT_BYTES)
        )
        val formatMatcher = FormatMatcher(mockGetFormatsUseCase(mockAdvertisementFormat))

        val filterByFormatUseCase = FilterByFormatUseCase(formatMatcher)
        val advertisementBytes = byteArrayOf(
            0x05.toByte(),
            0xFF.toByte(),
            0x59.toByte(),
            0x00.toByte(),
            0x17.toByte(),
            0xCF.toByte(),
            0x01.toByte()
            )

        val testScanResult =
            getTestScanResult("D1:D2:D3:D4:D5:D6", advertisementBytes)

        assertFalse(filterByFormatUseCase.execute(testScanResult))
    }

    private fun getTestScanResult(macAddress : String, rawData : ByteArray): ScanResult {
        return ScanResult(123456,macAddress,-50,rawData)
    }

    private fun mockAdvertisementFormat(
        requiredFormat: List<ByteFormatRequired>
    ): AdvertisementFormat {
        val mockedAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        Mockito.`when`(mockedAdvertisementFormat.getRequiredFormat()).thenReturn(requiredFormat)

        return mockedAdvertisementFormat
    }

    private fun mockGetFormatsUseCase(mockAdvertisementFormat: AdvertisementFormat): GetFormatsUseCase {
        val mockedGetFormatsUseCase = Mockito.mock(GetFormatsUseCase::class.java)
        Mockito.`when`(mockedGetFormatsUseCase.execute())
            .thenReturn(listOf(mockAdvertisementFormat))

        return mockedGetFormatsUseCase
    }

    private fun mockRequiredFormat(requiredBytes : ByteArray): List<ByteFormatRequired> {
        val list = mutableListOf<ByteFormatRequired>()

        requiredBytes.forEachIndexed { index, byte ->
            list.add(
                ByteFormatRequired(
                    "Name $index",
                    index,
                    byte,
                    0xFF.toByte()
                )
            )
        }

        return list
    }

}