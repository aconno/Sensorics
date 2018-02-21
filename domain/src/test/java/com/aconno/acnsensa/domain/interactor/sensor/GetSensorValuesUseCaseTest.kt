import com.aconno.acnsensa.domain.interactor.sensor.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.sensor.GetSensorValuesUseCase
import com.aconno.acnsensa.domain.model.Advertisement
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Single
import org.junit.Test

class GetSensorValuesUseCaseTest {
    @Test
    fun test() {
        val advertisementMatcher = AdvertisementMatcher()
        val getSensorValuesUseCase = GetSensorValuesUseCase(advertisementMatcher)

        val testDevice = Device("TestName", "D9:D9:D9:D9:D9:D9")

        val testRawData = listOf<Byte>(
            0x02,
            0x01,
            0x04,
            0x1A,
            0xFF.toByte(),
            0x59,
            0x00,
            0x17,
            0xCF.toByte(),
            0x00,
            0x2C,
            0x06,
            0xA8.toByte(),
            0x0E,
            0x4D,
            0x08,
            0x4A,
            0xFD.toByte(),
            0x61,
            0x06,
            0xC0.toByte(),
            0xB5.toByte(),
            0x92.toByte(),
            0x0D,
            0x50,
            0xFE.toByte(),
            0x33,
            0x09,
            0x00,
            0x00,
            0x00,
            0x00
        )
        val testAdvertisement = Advertisement(testRawData)
        val testScanResult = ScanResult(testDevice, testAdvertisement)

        val single: Single<Map<String, Number>> = getSensorValuesUseCase.execute(testScanResult)

        single.test().assertComplete()
    }
}