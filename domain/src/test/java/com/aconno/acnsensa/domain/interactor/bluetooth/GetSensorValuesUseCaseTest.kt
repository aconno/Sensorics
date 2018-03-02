import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.scalarsAdvertisement
import com.aconno.acnsensa.domain.vectorsAdvertisement
import io.reactivex.Single
import org.junit.Test

class GetSensorValuesUseCaseTest {

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