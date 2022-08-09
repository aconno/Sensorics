package com.aconno.sensorics.domain.time

import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class GetLocalTimeOfDayInSecondsUseCaseTest {

    private lateinit var getLocalTimeOfDayInSecondsUseCase: GetLocalTimeOfDayInSecondsUseCase

    @Mock
    lateinit var timeProvider: TimeProvider

    @Before
    fun setUp() {
        getLocalTimeOfDayInSecondsUseCase = GetLocalTimeOfDayInSecondsUseCase(
            timeProvider
        )
    }

    @Test
    fun testGetLocalTimeOfDayInSecondsUseCase() {
        getLocalTimeOfDayInSecondsUseCase.execute()

        Mockito.verify(timeProvider).getLocalTimeOfDayInSeconds()
        Mockito.verifyNoMoreInteractions(timeProvider)
    }
}