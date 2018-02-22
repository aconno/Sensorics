import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single
import org.junit.Test

class GetSensorValuesUseCaseTest {

    private val vectorsAdvertisement: List<Int> =
        listOf(
            0x02, 0x01, 0x04, 0x1A, 0xFF,
            0x59, 0x00, 0x17, 0xCF, 0x00,
            0x2C, 0x06, 0xA8, 0x0E, 0x4D,
            0x08, 0x4A, 0xFD, 0x61, 0x06,
            0xC0, 0xB5, 0x92, 0x0D, 0x50,
            0xFE, 0x33, 0x09, 0x00, 0x00
        )

    private val scalarsAdvertisement: List<Int> =
        listOf(
            0x02, 0x01, 0x04, 0x1A, 0xFF,
            0x59, 0x00, 0x17, 0xCF, 0x01,
            0xB9, 0x6D, 0xE3, 0x41, 0x28,
            0x54, 0x45, 0x42, 0xB2, 0x4E,
            0x80, 0x44, 0x82, 0x0C, 0x48,
            0x40, 0x2B, 0x09, 0x00, 0x00
        )

    @Test
    fun correctDeserializationOfScalarsAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val getSensorValuesUseCase = GetSensorValuesUseCase(advertisementMatcher)

        val testDevice = Device("TestName", "D9:D9:D9:D9:D9:D9")

        val testAdvertisement = Advertisement(scalarsAdvertisement.map { it.toByte() })
        val testScanResult = ScanResult(testDevice, testAdvertisement)

        val single: Single<Map<String, Number>> = getSensorValuesUseCase.execute(testScanResult)

        val result: List<Map<String, Number>> = single.test().values()
        result.forEach { println(it) }
    }

    @Test
    fun correctDeserializationOfVectorsAdvertisement() {
        val advertisementMatcher = AdvertisementMatcher()
        val getSensorValuesUseCase = GetSensorValuesUseCase(advertisementMatcher)

        val testDevice = Device("TestName", "D9:D9:D9:D9:D9:D9")

        val testAdvertisement = Advertisement(vectorsAdvertisement.map { it.toByte() })
        val testScanResult = ScanResult(testDevice, testAdvertisement)

        val single: Single<Map<String, Number>> = getSensorValuesUseCase.execute(testScanResult)

        val result: List<Map<String, Number>> = single.test().values()
        result.forEach { println(it) }
    }
}