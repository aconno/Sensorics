package de.troido.acnsensa.service

import de.troido.bleacon.service.ServiceBootBroadcastReceiver

/**
 * Starts the [SensorBleService] on device boot.
 */
class SensorBootBroadcastReceiver : ServiceBootBroadcastReceiver<SensorBleService>() {
    override val service = SensorBleService::class.java
}
