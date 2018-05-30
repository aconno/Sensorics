package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.TestUtils.INVALID_ADVERTISEMENT
import com.aconno.acnsensa.domain.TestUtils.SCALAR_ADVERTISEMENT
import com.aconno.acnsensa.domain.TestUtils.getTestScanResult
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Maybe
import org.junit.Test
import kotlin.test.assertTrue

class FilterAdvertisementsUseCaseTest {

    @Test
    fun filterAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)
        val testScanResult =
            getTestScanResult("Test name", "D9:D9:D9:D9:D9:D9", SCALAR_ADVERTISEMENT)

        val maybe: Maybe<ScanResult> = filterAdvertisementsUseCase.execute(testScanResult)

        assertTrue(maybe.test().valueCount() == 1)
    }

    @Test
    fun filterInvalidAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)
        val testScanResult =
            getTestScanResult("Test name", "D9:D9:D9:D9:D9:D9", INVALID_ADVERTISEMENT)

        val maybe: Maybe<ScanResult> = filterAdvertisementsUseCase.execute(testScanResult)

        assertTrue(maybe.test().valueCount() == 0)
    }
}