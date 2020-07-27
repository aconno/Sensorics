package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.model.Device
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ConnectionCharacteristicsFinderTest {

    private val supportedFormats = Mockito.spy(mutableListOf<Connection>())

    @Test
    fun hasCharacteristics_AppropriateDeviceTest() {
        val mockedConnection = mockAdvertisementFormat("Name", true)

        val device = mockDevice("Name", true)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase(listOf(mockedConnection)))

        assertTrue(finder.hasCharacteristics(device))
    }

    @Test
    fun hasCharacteristics_InappropriateDeviceTest() {
        val mockedConnection = mockAdvertisementFormat("Name", true)

        val device = mockDevice("Name", false)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase(listOf(mockedConnection)))

        assertFalse(finder.hasCharacteristics(device))
    }

    @Test
    fun addCharacteristicsToDevice_AppropriateDeviceTest() {
        val formats = mutableListOf<AdvertisementFormat>()
        for (i in 1..10) {
            formats.add(
                mockAdvertisementFormatWithWriteAndRead("Name $i", (i % 2) == 0)
            )
        }

        var device = mockDevice("Name 8", true)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase(formats))
        device = finder.addCharacteristicsToDevice(device)

        assertNotNull(device.connectionReadList)
        assertNotNull(device.connectionWriteList)
    }

    @Test
    fun addCharacteristicsToDevice_InappropriateDeviceTest() {
        val formats = mutableListOf<AdvertisementFormat>()
        for (i in 1..10) {
            formats.add(
                mockAdvertisementFormatWithWriteAndRead("Name $i", (i % 2) == 0)
            )
        }

        var device = mockDevice("Name 8", false)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase(formats))
        device = finder.addCharacteristicsToDevice(device)

        assertNull(device.connectionReadList)
        assertNull(device.connectionWriteList)
    }

    private fun mockGetFormatsUseCase(connections : List<AdvertisementFormat>? = null): GetFormatsUseCase {
        val mockAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        val mockedGetFormatsUseCase = Mockito.mock(GetFormatsUseCase::class.java)
        Mockito.`when`(mockedGetFormatsUseCase.execute())
            .thenReturn(connections ?: listOf(mockAdvertisementFormat))

        return mockedGetFormatsUseCase
    }


    private fun mockDevice(name: String, connectable: Boolean): Device {
        return Device(name, "", "", connectable = connectable)
    }

    private fun mockAdvertisementFormat(name: String, connectable: Boolean): AdvertisementFormat {
        val mockedFormat = Mockito.mock(AdvertisementFormat::class.java)
        Mockito.`when`(mockedFormat.getName()).thenReturn(name)
        Mockito.`when`(mockedFormat.isConnectible()).thenReturn(connectable)
        return mockedFormat
    }

    private fun mockAdvertisementFormatWithWriteAndRead(name: String, connectable: Boolean): AdvertisementFormat {
        val mockFormat = mockAdvertisementFormat(name, connectable)
        if (connectable) {
            Mockito.`when`(mockFormat.getConnectionReadList()).thenReturn(listOf())
            Mockito.`when`(mockFormat.getConnectionWriteList()).thenReturn(listOf())
        }
        return mockFormat
    }

}