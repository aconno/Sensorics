package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.repository.DeviceRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetSavedDevicesMaybeTest {

    private lateinit var getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase

    @Mock
    lateinit var mockDeviceRepository: DeviceRepository

    @Before
    fun setUp() {
        getSavedDevicesMaybeUseCase = GetSavedDevicesMaybeUseCase(
            mockDeviceRepository
        )
    }

    @Test
    fun testGetSavedDevicesMaybeUseCaseHappyCase() {
        getSavedDevicesMaybeUseCase.execute()

        verify(mockDeviceRepository).getAllDevicesMaybe()
        verifyNoMoreInteractions(mockDeviceRepository)
    }
}