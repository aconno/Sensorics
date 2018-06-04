package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.TestUtils.SCALAR_ADVERTISEMENT
import com.aconno.acnsensa.domain.TestUtils.getTestScanResult
import org.junit.Test
import kotlin.test.assertTrue

class FilterByMacAddressUseCaseTest {

    @Test
    fun advertisementMatches() {
        val filterByMacAddressUseCase = FilterByMacAddressUseCase()
        val testScanResult =
            getTestScanResult("Test name", "D9:D9:D9:D9:D9:D9", SCALAR_ADVERTISEMENT)

        val maybe = filterByMacAddressUseCase.execute(testScanResult, "D9:D9:D9:D9:D9:D9")

        assertTrue(maybe.test().valueCount() == 1)
    }

    @Test
    fun advertisementNotMatches(){
        val filterByMacAddressUseCase = FilterByMacAddressUseCase()
        val testScanResult =
            getTestScanResult("Test name", "CF:CF:CF:CF:CF:CF", SCALAR_ADVERTISEMENT)

        val maybe = filterByMacAddressUseCase.execute(testScanResult, "D9:D9:D9:D9:D9:D9")

        assertTrue(maybe.test().valueCount() == 0)
    }
}