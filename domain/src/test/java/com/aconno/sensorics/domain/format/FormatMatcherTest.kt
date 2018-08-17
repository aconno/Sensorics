package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.Util
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

        formatMatcher = FormatMatcher(listOf(mockAdvertisementFormat))
        assertTrue(formatMatcher.matches(Util.BEACON_BYTES))

        val foundFormat = formatMatcher.findFormat(bytes)!!
        assertEquals(mockAdvertisementFormat.getRequiredFormat(), foundFormat.getRequiredFormat())
    }

    @Test
    fun matches_UnsuitableTest() {

        val mockAdvertisementFormat = mockAdvertisementFormat(
            mockRequiredFormat(false)
        )

        formatMatcher = FormatMatcher(listOf(mockAdvertisementFormat))
        assertFalse(formatMatcher.matches(bytes))

        assertNull(formatMatcher.findFormat(bytes))
    }

    private fun mockAdvertisementFormat(
        requiredFormat: List<ByteFormatRequired>
    ): AdvertisementFormat {
        val mockedAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        Mockito.`when`(mockedAdvertisementFormat.getRequiredFormat()).thenReturn(requiredFormat)

        return mockedAdvertisementFormat
    }

    private fun mockFormat(): Map<String, ByteFormat> {
        val mockedMap = Mockito.spy(mapOf<String, ByteFormat>())
        Mockito.`when`(mockedMap.keys).thenReturn(setOf())
        return mockedMap
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
                    byte
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
                    byte or 0xAB.toByte()
                )
            )
        }
        return list
    }
}