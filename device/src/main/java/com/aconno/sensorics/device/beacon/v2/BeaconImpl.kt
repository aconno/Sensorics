package com.aconno.sensorics.device.beacon.v2

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.baseimpl.ArbitraryData
import com.aconno.sensorics.device.beacon.baseimpl.BeaconBaseImpl
import com.aconno.sensorics.device.beacon.baseimpl.BeaconSettingsFactory
import com.aconno.sensorics.device.beacon.v2.arbitrarydata.ArbitraryDataImpl
import com.aconno.sensorics.device.beacon.v2.parameters.ParametersImpl
import com.aconno.sensorics.device.beacon.v2.slots.SlotsImpl
import com.aconno.sensorics.domain.migrate.ValueConverters
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor

/**
 * Beacon device class
 *
 * @property device ble device to use for this device
 * @property name device name TODO: remove
 */
class BeaconImpl(
    taskProcessor: BluetoothTaskProcessor,
    var name: String? = ""
) : BeaconBaseImpl(taskProcessor, BEACON_SETTINGS_FACTORY) {

    companion object {
        val BEACON_SETTINGS_FACTORY = object : BeaconSettingsFactory {
            override fun createArbitraryData(): ArbitraryData = ArbitraryDataImpl()

            override fun createParameters(): Parameters = ParametersImpl()

            override fun createSlots(bytes: ByteArray?): Slots {
                if(bytes == null) return SlotsImpl(0)

                val slotCount: Int = ValueConverters.UINT32.deserialize(bytes, 4).toInt()

                val slots = SlotsImpl(slotCount)
                slots.fromBytes(bytes)

                return slots
            }

        }
    }
}