package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GeneralRestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RestPublishUseCasesTest {

    private lateinit var addRestPublishUseCase: AddRestPublishUseCase
    private lateinit var deleteRestPublishUseCase: DeleteRestPublishUseCase
    private lateinit var getAllEnabledRestPublishUseCase: GetAllEnabledRestPublishUseCase
    private lateinit var getAllRestPublishUseCase: GetAllRestPublishUseCase
    private lateinit var getRestPublishByIdUseCase: GetRestPublishByIdUseCase

    private val id: Long = 0

    private val restPublish: GeneralRestPublish = GeneralRestPublish(
        id,
        "Test",
        "www.test.com",
        "GET",
        true,
        "Millis",
        500,
        0,
        "DATA"
    )

    @Mock
    lateinit var mockRestPublishRepository: RestPublishRepository

    @Before
    fun setUp() {
        addRestPublishUseCase = AddRestPublishUseCase(
            mockRestPublishRepository
        )
        deleteRestPublishUseCase = DeleteRestPublishUseCase(
            mockRestPublishRepository
        )
        getAllEnabledRestPublishUseCase = GetAllEnabledRestPublishUseCase(
            mockRestPublishRepository
        )
        getAllRestPublishUseCase = GetAllRestPublishUseCase(
            mockRestPublishRepository
        )
        getRestPublishByIdUseCase = GetRestPublishByIdUseCase(
            mockRestPublishRepository
        )
    }

    @Test
    fun testAddRestPublishUseCase() {
        addRestPublishUseCase.execute(restPublish).blockingGet()

        verify(mockRestPublishRepository).addPublish(restPublish)
        verifyNoMoreInteractions(mockRestPublishRepository)
    }

    @Test
    fun testDeleteRestPublishUseCase() {
        deleteRestPublishUseCase.execute(restPublish).blockingGet()

        verify(mockRestPublishRepository).deletePublish(restPublish)
        verifyNoMoreInteractions(mockRestPublishRepository)
    }

    @Test
    fun testGetAllEnabledRestPublishUseCase() {
        getAllEnabledRestPublishUseCase.execute()

        verify(mockRestPublishRepository).allEnabled
        verifyNoMoreInteractions(mockRestPublishRepository)
    }

    @Test
    fun testGetAllRestPublishUseCase() {
        getAllRestPublishUseCase.execute()

        verify(mockRestPublishRepository).all
        verifyNoMoreInteractions(mockRestPublishRepository)
    }

    @Test
    fun testGetRestPublishByIdUseCase() {
        getRestPublishByIdUseCase.execute(id)

        verify(mockRestPublishRepository).getPublishById(id)
        verifyNoMoreInteractions(mockRestPublishRepository)
    }
}