package de.troido.acnsensa.service

import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.util.Log
import com.instacart.library.truetime.TrueTimeRx
import de.troido.acnsensa.data.*
import de.troido.acnsensa.persistence.csv.SensorRawCsvRecord
import de.troido.acnsensa.persistence.csv.persistToCsv
import de.troido.acnsensa.persistence.preferences.persistToPreferences
import de.troido.bleacon.beaconno.BeaconnoDevice
import de.troido.bleacon.beaconno.BeaconnoScanner
import de.troido.bleacon.beaconno.bleConnectionCallback
import de.troido.bleacon.config.scan.UUID16_TRANSFORM
import de.troido.bleacon.config.scan.bleFilter
import de.troido.bleacon.config.scan.scanSettings
import de.troido.bleacon.service.BleService
import de.troido.ekstend.android.debug.logD
import de.troido.ekstend.serial.toByteArray
import de.troido.ekstend.time.unixTime
import de.troido.ekstend.uuid.Uuid16
import java.nio.ByteOrder

private val BEACON_UUID = Uuid16.fromString("17CF")
private val NTP_SVC = Uuid16.fromString("55AA").toUuid()
private val NTP_CHR = Uuid16.fromString("33CC").toUuid()

/** Time period between two CSV writes, expressed in milliseconds. */
private const val CSV_WRITE_PERIOD = 1000L

/** Time period between two NTP write attempts. */
private const val NTP_WRITE_PERIOD = 5000L

class SensorBleService : BleService() {

    private var lastCsvWrite = 0L

    private var lastNtpWrite = 0L

    private val binder = SensorBinder()

    private val scanner = BeaconnoScanner(
            this,
            bleFilter(uuid16 = BEACON_UUID),
            sensorDeserializer,
            UUID16_TRANSFORM,
            scanSettings(scanMode = ScanSettings.SCAN_MODE_LOW_LATENCY)
    ) { _, device ->
        // In case of a malformed payload, data will be `null`.
        val data = device.data ?: return@BeaconnoScanner
        logD(data)
        val dataCsv = mapSensorRaw(data)
        handleRaw(dataCsv, device)
        persistToPreferences(data)
        binder.onData(data)
    }

    //TODO: This is a quick fix for storing values in CSV.
    private fun mapSensorRaw(data: SensorRaw): SensorCsv {
        var light = 0f
        var temperature = 0f
        var accelerometerX = 0f
        var accelerometerY = 0f
        var accelerometerZ = 0f
        var magnetometerX = 0f
        var magnetometerY = 0f
        var magnetometerZ = 0f

        data.asList().map {
            when (it) {
                is Light -> light = it.value
                is Temperature -> temperature = it.value
                is AccelerometerAxis -> {
                    when (it.axis) {
                        Axis.X -> accelerometerX = it.value
                        Axis.Y -> accelerometerY = it.value
                        Axis.Z -> accelerometerZ = it.value
                    }
                }
                is MagnetometerAxis -> {
                    when (it.axis) {
                        Axis.X -> magnetometerX = it.value
                        Axis.Y -> magnetometerY = it.value
                        Axis.Z -> magnetometerZ = it.value
                    }
                }
            }
        }

        val time: Int = System.currentTimeMillis().toInt()
        return SensorCsv(
                0,
                time,
                SensorSet(Light(light),
                        Temperature(temperature),
                        AccelerometerAxis(accelerometerX, Axis.X),
                        AccelerometerAxis(accelerometerY, Axis.Y),
                        AccelerometerAxis(accelerometerZ, Axis.Z),
                        MagnetometerAxis(magnetometerX, Axis.X),
                        MagnetometerAxis(magnetometerY, Axis.Y),
                        MagnetometerAxis(magnetometerZ, Axis.Z)))
    }


    private fun handleRaw(data: SensorCsv, device: BeaconnoDevice<SensorData>) {
        val correctedData = if (data.time == 0) data.copy(time = correctTime()) else data
        if (data.time == 0) ntpWriteGate { writeNtp(device) }

        binder.onData(correctedData)
        Log.e("SENSOR", "Write to CSV")
        csvWriteGate { persistToCsv(SensorRawCsvRecord(correctedData)) }
    }

    override val actors = listOf(scanner)

    override fun onBind(intent: Intent?): SensorBinder = binder

    /** If beacon time is not set, it requires NTP time to be written to it. */
    private fun writeNtp(device: BeaconnoDevice<SensorData>) {
        // If beacon time is not set, it requires NTP time to be written to it.
        if (TrueTimeRx.isInitialized()) {
            device.connect(this, bleConnectionCallback(NTP_SVC, NTP_CHR) {
                it.write(correctTime().toByteArray(ByteOrder.LITTLE_ENDIAN))
            })
        }
    }

    private fun ntpWriteGate(block: () -> Unit) {
        val time = System.currentTimeMillis()
        if (time - lastNtpWrite > NTP_WRITE_PERIOD) {
            block()
            lastNtpWrite = time
        }
    }

    private fun csvWriteGate(block: () -> Unit) {
        val time = System.currentTimeMillis()
        if (time - lastCsvWrite > CSV_WRITE_PERIOD) {
            block()
            lastCsvWrite = time
        }
    }

    /**
     * If the beacon's data is not set (indicated by a time of zero), we use the NTP time,
     * or if it's not initialized yet, we settle down for the device's time
     * ([System.currentTimeMillis]).
     */
    private fun correctTime(): Int = when {
        TrueTimeRx.isInitialized() -> TrueTimeRx.now().unixTime
        else -> unixTime()
    }
}
