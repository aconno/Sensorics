package com.aconno.sensorics.domain.interactor.consolidation

import com.aconno.sensorics.domain.Util
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.model.ScanResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GenerateScanDeviceUseCaseTest {

    private lateinit var generateScanDeviceUseCase: GenerateScanDeviceUseCase

    @Before
    fun setup() {
        val params = mockAdvertisementFormat()
        val formatMatcher: FormatMatcher = Mockito.mock(FormatMatcher::class.java)
        Mockito.`when`(formatMatcher.findFormat(Util.BEACON_BYTES))
            .thenReturn(params)

        generateScanDeviceUseCase = GenerateScanDeviceUseCase(formatMatcher)
    }

    @Test
    fun execute_Test() {
        //Mocking ScanResult
        val scanResult: ScanResult = Mockito.mock(ScanResult::class.java)
        Mockito.`when`(scanResult.rawData)
            .thenReturn(Util.BEACON_BYTES)

        Mockito.`when`(scanResult.macAddress)
            .thenReturn("MA:CA:DD:RE:SS")

        Mockito.`when`(scanResult.rssi)
            .thenReturn(-73)

        val test = generateScanDeviceUseCase.execute(scanResult)
            .test()

        test.awaitTerminalEvent()

        test
            .assertNoErrors()
            .assertValue { it.rssi == -73 }
            .assertValue { it.device.macAddress == "MA:CA:DD:RE:SS" } //Battery Level
    }

    private fun mockAdvertisementFormat(): AdvertisementFormat {
        val mockedAdvertisementFormat = Mockito.mock(AdvertisementFormat::class.java)

        Mockito.`when`(mockedAdvertisementFormat.getIcon()).thenReturn("Icon")
        Mockito.`when`(mockedAdvertisementFormat.getName()).thenReturn("AdvertisementFormat")

        return mockedAdvertisementFormat
    }
}