package de.troido.acnsensa.service

import android.bluetooth.le.ScanSettings
import android.content.Intent
import de.troido.acnsensa.persistence.preferences.persistToPreferences
import de.troido.bleacon.beaconno.BeaconnoScanner
import de.troido.bleacon.config.scan.UUID16_TRANSFORM
import de.troido.bleacon.config.scan.bleFilter
import de.troido.bleacon.config.scan.scanSettings
import de.troido.bleacon.service.BleService
import de.troido.ekstend.android.debug.logD
import de.troido.ekstend.uuid.Uuid16

private val BEACON_UUID = Uuid16.fromString("17CF")

class SensorBleService : BleService() {

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
        persistToPreferences(data)
        binder.onData(data)
    }

    override val actors = listOf(scanner)

    override fun onBind(intent: Intent?): SensorBinder = binder
}
