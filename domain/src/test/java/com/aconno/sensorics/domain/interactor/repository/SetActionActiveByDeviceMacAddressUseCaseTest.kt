package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionsByDeviceMacAddressUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.SetActionActiveByDeviceMacAddressUseCase
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

// TODO: Fix failing tests
@RunWith(MockitoJUnitRunner::class)
class SetActionActiveByDeviceMacAddressUseCaseTest {

    private val address = "AA:BB:CC:DD:EE:FF"

    private lateinit var mockAddActionUseCase: AddActionUseCase
    private lateinit var mockGetActionsByDeviceMacAddressUseCase: GetActionsByDeviceMacAddressUseCase
    private lateinit var mockSetActionActiveByDeviceMacAddressUseCase: SetActionActiveByDeviceMacAddressUseCase

    @Mock
    lateinit var mockActionsRepository: ActionsRepository

    @Before
    fun setUp() {
        mockAddActionUseCase = AddActionUseCase(
            mockActionsRepository
        )
        mockGetActionsByDeviceMacAddressUseCase = GetActionsByDeviceMacAddressUseCase(
            mockActionsRepository
        )
        mockSetActionActiveByDeviceMacAddressUseCase = SetActionActiveByDeviceMacAddressUseCase(
            mockAddActionUseCase, mockGetActionsByDeviceMacAddressUseCase
        )
    }

    @Ignore("Failing test")
    @Test
    fun testSetActionActiveByDeviceUseCase() {
        mockSetActionActiveByDeviceMacAddressUseCase.execute(address, true)

        Mockito.verify(mockActionsRepository).getActionsByDeviceMacAddress(address)
        Mockito.verifyNoMoreInteractions(mockActionsRepository)
    }
}