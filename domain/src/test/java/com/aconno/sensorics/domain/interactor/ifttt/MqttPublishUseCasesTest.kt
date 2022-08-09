package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GeneralMqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeletePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllEnabledPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MqttPublishUseCasesTest {

    private lateinit var addMqttPublishUseCase: AddPublishUseCase<MqttPublish>
    private lateinit var deleteMqttPublishUseCase: DeletePublishUseCase<MqttPublish>
    private lateinit var getAllMqttPublishUseCase: GetAllPublishUseCase<MqttPublish>
    private lateinit var getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase
    private lateinit var getAllEnabledMqttPublishUseCase : GetAllEnabledPublishUseCase<MqttPublish>

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
        addMqttPublishUseCase = AddPublishUseCase(
            mockMqttPublishRepository
        )
        deleteMqttPublishUseCase = DeletePublishUseCase(
            mockMqttPublishRepository
        )
        getAllEnabledMqttPublishUseCase = GetAllEnabledPublishUseCase(
            mockMqttPublishRepository
        )
        getAllMqttPublishUseCase = GetAllPublishUseCase(
            mockMqttPublishRepository
        )
        getMqttPublishByIdUseCase = GetMqttPublishByIdUseCase(
            mockMqttPublishRepository
        )
    }

    @Test
    fun testAddMqttPublishUseCase() {
        addMqttPublishUseCase.execute(mqttPublish).blockingGet()

        verify(mockMqttPublishRepository).addPublish(mqttPublish)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testDeleteMqttPublishUseCase() {
        deleteMqttPublishUseCase.execute(mqttPublish).blockingGet()

        verify(mockMqttPublishRepository).deletePublish(mqttPublish)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetAllEnabledMqttPublishUseCase() {
        getAllEnabledMqttPublishUseCase.execute()

        verify(mockMqttPublishRepository).allEnabled
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetAllMqttPublishUseCase() {
        getAllMqttPublishUseCase.execute()

        verify(mockMqttPublishRepository).all
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }

    @Test
    fun testGetMqttPublishByIdUseCase() {
        getMqttPublishByIdUseCase.execute(id)

        verify(mockMqttPublishRepository).getPublishById(id)
        verifyNoMoreInteractions(mockMqttPublishRepository)
    }
}