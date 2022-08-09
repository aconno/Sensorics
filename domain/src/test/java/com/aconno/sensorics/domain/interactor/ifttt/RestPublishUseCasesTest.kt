package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GeneralRestPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeletePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllEnabledPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RestPublishUseCasesTest {

    private lateinit var addRestPublishUseCase: AddPublishUseCase<RestPublish>
    private lateinit var deleteRestPublishUseCase: DeletePublishUseCase<RestPublish>
    private lateinit var getAllRestPublishUseCase: GetAllPublishUseCase<RestPublish>
    private lateinit var getRestPublishByIdUseCase: GetRestPublishByIdUseCase
    private lateinit var getAllEnabledRestPublishUseCase : GetAllEnabledPublishUseCase<RestPublish>

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
        addRestPublishUseCase = AddPublishUseCase(
            mockRestPublishRepository
        )
        deleteRestPublishUseCase = DeletePublishUseCase(
            mockRestPublishRepository
        )
        getAllEnabledRestPublishUseCase = GetAllEnabledPublishUseCase(
            mockRestPublishRepository
        )
        getAllRestPublishUseCase = GetAllPublishUseCase(
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