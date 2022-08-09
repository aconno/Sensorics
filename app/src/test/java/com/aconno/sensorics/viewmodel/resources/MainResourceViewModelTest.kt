package com.aconno.sensorics.viewmodel.resources

import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class MainResourceViewModelTest {

    private lateinit var viewModel: MainResourceViewModel
    private lateinit var getMainResourceUseCase: GetMainResourceUseCase

    @Before
    fun createViewModel() {
        getMainResourceUseCase = Mockito.mock(GetMainResourceUseCase::class.java)
        viewModel = MainResourceViewModel(getMainResourceUseCase)
    }

    @Test
    fun getResourcePath_shouldReturnResourcePath() {
        val deviceName = "AcnSensa"
        val resourcePath = "file:///android_asset/device_screens/acnsensa/acnsensa.html"

        Mockito.`when`(getMainResourceUseCase.execute(deviceName))
            .thenReturn(Single.just(resourcePath))

        viewModel.getResourcePath(deviceName)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { it == resourcePath }
    }

    @Test
    fun getResourcePath_shouldThrowError() {
        val deviceName = "Unknown device"
        val exception = Exception()

        Mockito.`when`(getMainResourceUseCase.execute(deviceName))
            .thenReturn(Single.error(exception))

        viewModel.getResourcePath(deviceName)
            .test()
            .assertError(exception)
            .assertNotComplete()
    }
}