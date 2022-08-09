package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionsByDeviceMacAddressUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class GetActionsByDeviceMacAddressUseCaseTest {

    private val address = "AA:BB:CC:DD:EE:FF"

    private lateinit var mockGetActionsByDeviceMacAddressUseCase: GetActionsByDeviceMacAddressUseCase

    @Mock
    lateinit var mockActionsRepository: ActionsRepository

    @Before
    fun setUp() {
        mockGetActionsByDeviceMacAddressUseCase = GetActionsByDeviceMacAddressUseCase(
            mockActionsRepository
        )
    }

    @Test
    fun testGetActionsByDeviceMacAddress() {
        mockGetActionsByDeviceMacAddressUseCase.execute(address)

        Mockito.verify(mockActionsRepository).getActionsByDeviceMacAddress(address)
        Mockito.verifyNoMoreInteractions(mockActionsRepository)
    }
}