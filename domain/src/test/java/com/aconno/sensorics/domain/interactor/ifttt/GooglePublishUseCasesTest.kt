package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.GeneralGooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GooglePublishUseCasesTest {
    private lateinit var addGooglePublishUseCase: AddPublishUseCase<GooglePublish>
    private lateinit var getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase
    private lateinit var deleteGooglePublishUseCase : DeletePublishUseCase<GooglePublish>
    private lateinit var getAllEnabledGooglePublishUseCase : GetAllEnabledPublishUseCase<GooglePublish>
    private lateinit var getAllGooglePublishUseCase : GetAllPublishUseCase<GooglePublish>

    private val id: Long = 0

    private val googlePublish: GeneralGooglePublish = GeneralGooglePublish(
        id,
        "Test",
        "1234",
        "US",
        "reg",
        "00:11:22:33:44:55",
        "pkey",
        true,
        "Millis",
        500,
        0,
        "DATA"
    )

    @Mock
    lateinit var mockGooglePublishRepository: GooglePublishRepository

    @Before
    fun setUp() {
        addGooglePublishUseCase = AddPublishUseCase(
            mockGooglePublishRepository
        )
        deleteGooglePublishUseCase = DeletePublishUseCase(
            mockGooglePublishRepository
        )
        getAllEnabledGooglePublishUseCase = GetAllEnabledPublishUseCase(
            mockGooglePublishRepository
        )
        getAllGooglePublishUseCase = GetAllPublishUseCase(
            mockGooglePublishRepository
        )
        getGooglePublishByIdUseCase = GetGooglePublishByIdUseCase(
            mockGooglePublishRepository
        )
    }

    @Test
    fun testAddGooglePublishUseCase() {
        addGooglePublishUseCase.execute(googlePublish).blockingGet()

        verify(mockGooglePublishRepository).addPublish(googlePublish)
        verifyNoMoreInteractions(mockGooglePublishRepository)
    }

    @Test
    fun testDeleteGooglePublishUseCase() {
        deleteGooglePublishUseCase.execute(googlePublish).blockingGet()

        verify(mockGooglePublishRepository).deletePublish(googlePublish)
        verifyNoMoreInteractions(mockGooglePublishRepository)
    }

    @Test
    fun testGetAllEnabledGooglePublishUseCase() {
        getAllEnabledGooglePublishUseCase.execute()

        verify(mockGooglePublishRepository).allEnabled
        verifyNoMoreInteractions(mockGooglePublishRepository)
    }

    @Test
    fun testGetAllGooglePublishUseCase() {
        getAllGooglePublishUseCase.execute()

        verify(mockGooglePublishRepository).all
        verifyNoMoreInteractions(mockGooglePublishRepository)
    }

    @Test
    fun testGetGooglePublishByIdUseCase() {
        getGooglePublishByIdUseCase.execute(id)

        verify(mockGooglePublishRepository).getPublishById(id)
        verifyNoMoreInteractions(mockGooglePublishRepository)
    }
}