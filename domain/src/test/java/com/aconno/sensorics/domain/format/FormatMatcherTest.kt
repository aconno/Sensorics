package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.Util
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.experimental.or
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class FormatMatcherTest {

    private lateinit var formatMatcher: FormatMatcher
    private val supportedFormats: MutableList<AdvertisementFormat> = mutableListOf()
    private val bytes = Util.REQUIRED_FORMAT_BYTES

    @Before
    fun setup() {
        for (i in 1..10) {
            supportedFormats.add(
                mockAdvertisementFormat(
                    mockRequiredFormat(true)
                )
            )
        }
    }

    @Test
    fun matches_SuitableTest() {

        val mockAdvertisementFormat = mockAdvertisementFormat(
            mockRequiredFormat(true)
        )

        formatMatcher = FormatMatcher(mockGetFormatsUseCase(mockAdvertisementFormat))
        assertTrue(formatMatcher.matches(Util.BEACON_BYTES))

        val foundFormat = formatMatcher.findFormat(Util.BEACON_BYTES)!!
        assertEquals(mockAdvertisementFormat.getRequiredFormat(), foundFormat.getRequiredFormat())
    }

    @Test
    fun matches_UnsuitableTest() {

        val mockAdvertisementFormat = mockAdvertisementFormat(
            mockRequiredFormat(false)
        )

        formatMatcher = FormatMatcher(mockGetFormatsUseCase(mockAdvertisementFormat))
        assertFalse(formatMatcher.matches(Util.BEACON_BYTES))

        assertNull(formatMatcher.findFormat(Util.BEACON_BYTES))
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

    private fun mockRequiredFormat(isSuitable: Boolean): List<ByteFormatRequired> {
        return if (isSuitable) {
            getSuitableList()
        } else {
            getUnSuitableList()
        }
    }

    private fun getSuitableList(): List<ByteFormatRequired> {
        val list = mutableListOf<ByteFormatRequired>()

        bytes.forEachIndexed { index, byte ->
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

    private fun getUnSuitableList(): List<ByteFormatRequired> {
        val list = mutableListOf<ByteFormatRequired>()

        bytes.forEachIndexed { index, byte ->
            list.add(
                ByteFormatRequired(
                    "Name $index",
                    index,
                    byte or 0xAB.toByte(),
                    0xFF.toByte()
                )
            )
        }
        return list
    }
}