package com.aconno.sensorics.ui.settings_framework

import com.aconno.sensorics.device.beacon.Beacon
import java.io.Serializable

class BeaconSettingsInfo : Serializable {

    var name: String? = ""
    var address: String = "00:00:00:00:00:00"
    var connectible: Boolean = true
    var manufacturer: String = "Aconno"
    var model: String = "V1"
    var softwareVersion: String = "1"
    var hardwareVersion: String = "1"
    var firmwareVersion: String = "1"
    var osVersion: String = "1"
    var advFeature: String = "N/A"
    var supportedTxPower: String? = ""
    var supportedSlots: String? = ""
    var slotAmount: Int = 0


    class Builder {
        fun build(beacon: Beacon?): BeaconSettingsInfo {
            val beaconInfo = BeaconSettingsInfo()
            beacon?.let { currentBeacon ->
                beaconInfo.address = currentBeacon.mac
                beaconInfo.manufacturer = currentBeacon.manufacturer
                beaconInfo.model = currentBeacon.model
                beaconInfo.softwareVersion = currentBeacon.sdkVersion
                beaconInfo.hardwareVersion = currentBeacon.hwVersion
                beaconInfo.firmwareVersion = currentBeacon.fwVersion
                beaconInfo.osVersion = currentBeacon.freeRTOSVersion
                beaconInfo.supportedTxPower = currentBeacon.supportedTxPowersString
                beaconInfo.supportedSlots = currentBeacon.slots.joinToString(", ") { it.name }
                beaconInfo.slotAmount = currentBeacon.slotCount.toInt()
            }
            return beaconInfo

        }
    }
}