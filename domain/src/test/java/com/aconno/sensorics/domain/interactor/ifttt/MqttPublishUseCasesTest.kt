package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GeneralMqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MqttPublishUseCasesTest {

    private lateinit var addMqttPublishUseCase: AddMqttPublishUseCase
    private lateinit var deleteMqttPublishUseCase: DeleteMqttPublishUseCase
    private lateinit var getAllEnabledMqttPublishUseCase: GetAllEnabledMqttPublishUseCase
    private lateinit var getAllMqttPublishUseCase: GetAllMqttPublishUseCase
    private lateinit var getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase

    private val id: Long = 0

    private val mqttPublish: GeneralMqttPublish = GeneralMqttPublish(
        id,
        "name",
        "tcp://whatever",
        "cliid",
        "user",
        "pass",
        "topic",
        0,
        true,
        "Millis",
        500,
        0,
        "data"
    )

    @Mock
    lateinit var mockMqttPublishRepository: MqttPublishRepository

    @Before
    fun setUp() {
        addMqttPublishUseCase = AddMqttPublishUseCase(
            mockMqttPublishRepository
        )
        deleteMqttPublishUseCase = DeleteMqttPublishUseCase(
            mockMqttPublishRepository
        )
        getAllEnabledMqttPublishUseCase = GetAllEnabledMqttPublishUseCase(
            mockMqttPublishRepository
        )
        getAllMqttPublishUseCase = GetAllMqttPublishUseCase(
            mockMqttPublishRepository
        )
        getMqttPublishByIdUseCase = GetMqttPublishByIdUseCase(
            mockMqttPublishRepository
        )
    }

    @Test
    fun testAddMqttPublishUseCase() {
        addMqttPublishUseCase.execute(mqttPublish).blockingGet()

        verify(mockMqttPublishRepository).addMqttPublish(mqttPublish)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testDeleteMqttPublishUseCase() {
        deleteMqttPublishUseCase.execute(mqttPublish).blockingGet()

        verify(mockMqttPublishRepository).deleteMqttPublish(mqttPublish)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetAllEnabledMqttPublishUseCase() {
        getAllEnabledMqttPublishUseCase.execute()

        verify(mockMqttPublishRepository).getAllEnabledMqttPublish()
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetAllMqttPublishUseCase() {
        getAllMqttPublishUseCase.execute()

        verify(mockMqttPublishRepository).getAllMqttPublish()
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetMqttPublishByIdUseCase() {
        getMqttPublishByIdUseCase.execute(id)

        verify(mockMqttPublishRepository).getMqttPublishById(id)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }
}