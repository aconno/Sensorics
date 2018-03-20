package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.invalidAdvertisement
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.scalarsAdvertisement
import io.reactivex.Maybe
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author aconno
 */
class FilterAdvertisementsUseCaseTest {

    @Test
    fun filterAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)

        val testDevice = Device("TestName", "D9:D9:D9:D9:D9:D9")

        val testAdvertisement = Advertisement(scalarsAdvertisement.map { it.toByte() })
        val testScanResult = ScanResult(testDevice, testAdvertisement)

        val maybe: Maybe<ScanResult> = filterAdvertisementsUseCase.execute(testScanResult)

        assertTrue(maybe.test().valueCount() == 1)
    }

    @Test
    fun filterInvalidAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val filterAdvertisementsUseCase = FilterAdvertisementsUseCase(advertisementMatcher)

        val testDevice = Device("TestName", "D9:D9:D9:D9:D9:D9")

        val testAdvertisement = Advertisement(invalidAdvertisement.map { it.toByte() })
        val testScanResult = ScanResult(testDevice, testAdvertisement)

        val maybe: Maybe<ScanResult> = filterAdvertisementsUseCase.execute(testScanResult)

        assertTrue(maybe.test().valueCount() == 0)
    }
}