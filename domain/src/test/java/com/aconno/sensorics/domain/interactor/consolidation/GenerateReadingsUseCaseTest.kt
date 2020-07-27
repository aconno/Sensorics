package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.Util
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import com.aconno.sensorics.domain.serialization.Deserializer
import com.aconno.sensorics.domain.serialization.DeserializerImpl
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GenerateReadingsUseCaseTest {

    private lateinit var generateReadingsUseCase: GenerateReadingsUseCase

    @Before
    fun setup() {
        val params = mockAdvertisementFormat()
        val formatMatcher: FormatMatcher = Mockito.mock(FormatMatcher::class.java)
        Mockito.`when`(formatMatcher.findFormat(Util.BEACON_BYTES))
            .thenReturn(params)

        val deserializer: Deserializer = DeserializerImpl()

        val deviceGroupRepository = Mockito.mock(DeviceGroupRepository::class.java)
        Mockito.`when`(deviceGroupRepository.getAllDeviceGroups()).thenReturn(Single.just(listOf()))
        val deviceGroupDeviceJoinRepository = Mockito.mock(DeviceGroupDeviceJoinRepository::class.java)
        Mockito.`when`(deviceGroupDeviceJoinRepository.getAllDeviceGroupDeviceRelations()).thenReturn(
            Single.just(listOf()))


        generateReadingsUseCase = GenerateReadingsUseCase(formatMatcher, deserializer,deviceGroupDeviceJoinRepository,deviceGroupRepository)
    }

    @Test
    fun execute_Test() {
        //Mocking ScanResult
        val scanResult: ScanResult = Mockito.mock(ScanResult::class.java)
        Mockito.`when`(scanResult.rawData)
            .thenReturn(Util.BEACON_BYTES)
        Mockito.`when`(scanResult.macAddress)
            .thenReturn("MA:CA:DD:RE:SS")

        val test = generateReadingsUseCase.execute(scanResult)
            .test()

        test.awaitTerminalEvent()

        test
            .assertNoErrors()
            .assertValue { it.isNotEmpty() }
            .assertValue { it[0].value == (100).toByte() } //Battery Level
            .assertValue { it[1].value == 37.375f } // Temperature
            .assertValue { it[2].value == 1030.8435f } // Pressure
            .assertValue { it[3].value == 28.320312f } // Light
            .assertValue { it[4].value == 29.785675f } // Humidity
    }

    private fun mockAdvertisementFormat(): AdvertisementFormat {
        val mockedAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        Mockito.`when`(mockedAdvertisementFormat.getFormat()).thenReturn(Util.getListOfFormats())
        Mockito.`when`(mockedAdvertisementFormat.getIcon()).thenReturn("Icon")
        Mockito.`when`(mockedAdvertisementFormat.getName()).thenReturn("AdvertisementFormat")
        Mockito.`when`(mockedAdvertisementFormat.id).thenReturn("012345")

        return mockedAdvertisementFormat
    }
}