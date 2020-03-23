package com.aconno.sensorics.device.beacon.protobuffers

import com.aconno.sensorics.device.beacon.Parameters
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.device.beacon.baseimpl.ArbitraryData
import com.aconno.sensorics.device.beacon.baseimpl.BeaconBaseImpl
import com.aconno.sensorics.device.beacon.baseimpl.BeaconSettingsFactory
import com.aconno.sensorics.device.beacon.protobuffers.arbitrarydata.ArbitraryDataProtobufImpl
import com.aconno.sensorics.device.beacon.protobuffers.parameters.ParametersProtobufImpl
import com.aconno.sensorics.device.beacon.protobuffers.slots.SlotsProtobufImpl
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor

class BeaconProtobufImpl(
    taskProcessor: BluetoothTaskProcessor,
    var name : String? = ""
) : BeaconBaseImpl(taskProcessor, BEACON_SETTINGS_FACTORY){

    companion object {
        val BEACON_SETTINGS_FACTORY = object : BeaconSettingsFactory {
            override fun createArbitraryData(): ArbitraryData = ArbitraryDataProtobufImpl()

            override fun createParameters(): Parameters = ParametersProtobufImpl()

            override fun createSlots(bytes: ByteArray?): Slots {
                val slots = SlotsProtobufImpl(0)
                bytes?.let {
                    slots.fromBytes(it)
                }

                return slots
            }

        }
    }
}