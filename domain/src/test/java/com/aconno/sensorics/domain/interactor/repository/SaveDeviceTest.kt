package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SaveDeviceTest {

    private val device: Device = Device(
        "Name", "Alias", "MA:CA:DD:RE:SS", ""
    )

    private lateinit var saveDeviceUseCase: SaveDeviceUseCase

    @Mock
    lateinit var mockDeviceRepository: DeviceRepository

    @Before
    fun setUp() {
        saveDeviceUseCase = SaveDeviceUseCase(
            mockDeviceRepository
        )
    }

    @Test
    fun testSaveDeviceUseCaseHappyCase() {
        saveDeviceUseCase.execute(device)

        Mockito.verify(mockDeviceRepository).insertDevice(device)
        Mockito.verifyNoMoreInteractions(mockDeviceRepository)
    }
}