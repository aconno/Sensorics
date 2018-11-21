package com.aconno.sensorics.domain.format

import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.model.Device
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
        val mockedConnection = mockConnection("Name", true)

        supportedFormats.add(mockedConnection)

        val device = mockDevice("Name", true)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase())

        Mockito.verify(supportedFormats).add(mockedConnection)
        assertTrue(finder.hasCharacteristics(device))
    }

    @Test
    fun hasCharacteristics_InappropriateDeviceTest() {
        val mockedConnection = mockConnection("Name", true)

        supportedFormats.add(mockedConnection)

        val device = mockDevice("Name", false)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase())

        Mockito.verify(supportedFormats).add(mockedConnection)
        assertFalse(finder.hasCharacteristics(device))
    }

    @Test
    fun addCharacteristicsToDevice_AppropriateDeviceTest() {

        for (i in 1..10) {
            supportedFormats.add(
                mockConnectionWithWriteAndRead("Name $i", (i % 2) == 0)
            )
        }

        var device = mockDevice("Name 8", true)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase())
        device = finder.addCharacteristicsToDevice(device)

        assertNotNull(device.connectionReadList)
        assertNotNull(device.connectionWriteList)
    }

    @Test
    fun addCharacteristicsToDevice_InappropriateDeviceTest() {

        for (i in 1..10) {
            supportedFormats.add(
                mockConnectionWithWriteAndRead("Name $i", (i % 2) == 0)
            )
        }

        var device = mockDevice("Name 8", false)

        val finder = ConnectionCharacteristicsFinderImpl(mockGetFormatsUseCase())
        device = finder.addCharacteristicsToDevice(device)

        assertNull(device.connectionReadList)
        assertNull(device.connectionWriteList)
    }

    private fun mockGetFormatsUseCase(): GetFormatsUseCase {
        val mockAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        val mockedGetFormatsUseCase = Mockito.mock(GetFormatsUseCase::class.java)
        Mockito.`when`(mockedGetFormatsUseCase.execute())
            .thenReturn(listOf(mockAdvertisementFormat))

        return mockedGetFormatsUseCase
    }


    private fun mockDevice(name: String, connectable: Boolean): Device {
        return Device(name, "", "", connectable = connectable)
    }

    private fun mockConnection(name: String, connectable: Boolean): Connection {
        val mockedConnection = Mockito.mock(Connection::class.java)
        Mockito.`when`(mockedConnection.getName()).thenReturn(name)
        Mockito.`when`(mockedConnection.isConnectible()).thenReturn(connectable)
        return mockedConnection
    }

    private fun mockConnectionWithWriteAndRead(name: String, connectable: Boolean): Connection {
        val mockConnection = mockConnection(name, connectable)
        if (connectable) {
            Mockito.`when`(mockConnection.getConnectionReadList()).thenReturn(listOf())
            Mockito.`when`(mockConnection.getConnectionWriteList()).thenReturn(listOf())
        }
        return mockConnection
    }

}