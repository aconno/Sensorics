package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.TestUtils.SCALAR_ADVERTISEMENT
import com.aconno.acnsensa.domain.TestUtils.VECTOR_ADVERTISEMENT
import com.aconno.acnsensa.domain.TestUtils.getTestScanResult
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import io.reactivex.Single
import org.junit.Test

class DeserializeScanResultUseCaseTest {

    @Test
    fun correctDeserializationOfScalarsAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val deserializeScanResultUseCase = DeserializeScanResultUseCase(advertisementMatcher)
        val testScanResult =
            getTestScanResult("Test name", "D9:D9:D9:D9:D9:D9", SCALAR_ADVERTISEMENT)

        val single: Single<Map<String, Number>> =
            deserializeScanResultUseCase.execute(testScanResult)

        val result: List<Map<String, Number>> = single.test().values()
        result.forEach { println(it) }
    }

    @Test
    fun correctDeserializationOfVectorsAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val deserializeScanResultUseCase = DeserializeScanResultUseCase(advertisementMatcher)
        val testScanResult =
            getTestScanResult("Test name", "D9:D9:D9:D9:D9:D9", VECTOR_ADVERTISEMENT)

        val single: Single<Map<String, Number>> =
            deserializeScanResultUseCase.execute(testScanResult)

        val result: List<Map<String, Number>> = single.test().values()
        result.forEach { println(it) }
    }
}