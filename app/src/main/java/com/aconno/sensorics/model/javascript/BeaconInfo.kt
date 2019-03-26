package com.aconno.sensorics.model.javascript

import com.aconno.bluetooth.beacon.Beacon
import java.io.Serializable

class BeaconInfo : Serializable {
    var name: String? = ""
    var address: String = "00:00:00:00:00:00"
    var connectible: Boolean = true
    var rssi: Int = 0
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
        fun build(beacon: Beacon?): BeaconInfo {
            val beaconInfo = BeaconInfo()
            beacon?.let { currentBeacon ->
                beaconInfo.name = currentBeacon.name
                beaconInfo.address = currentBeacon.mac
                beaconInfo.connectible = currentBeacon.connectible
                beaconInfo.manufacturer = currentBeacon.manufacturer
                beaconInfo.model = currentBeacon.model
                beaconInfo.softwareVersion = currentBeacon.sdkVersion
                beaconInfo.hardwareVersion = currentBeacon.hwVersion
                beaconInfo.firmwareVersion = currentBeacon.fwVersion
                beaconInfo.osVersion = currentBeacon.freeRTOSVersion
                beaconInfo.advFeature = currentBeacon.advFeature
                beaconInfo.supportedTxPower = currentBeacon.supportedTxPower.joinToString(", ")
                beaconInfo.supportedSlots =
                    currentBeacon.supportedSlots.joinToString(", ") { it.name }
                beaconInfo.slotAmount = currentBeacon.slots.size
            }
            return beaconInfo

        }
    }


}