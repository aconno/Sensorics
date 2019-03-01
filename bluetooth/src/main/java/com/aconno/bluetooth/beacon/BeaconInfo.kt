package com.aconno.bluetooth.beacon

import java.io.Serializable

class BeaconInfo: Serializable {
    var name: String? = ""
    var address: String = "00:00:00:00:00:00"
    var connectible: Boolean = true
    var rssi: Int = 0
    var manufacturer: String = "Aconno"
    var model: String = "V1"
    var softwareVersion: String = "1"
    var hardwareVersion: String = "1"
    var firmwareVersion: String = "1"
    var advFeature: String = "N/A"
    var supportedTxPower: String? = ""
    var supportedSlots: String? = ""
    var slotAmount: Int = 0


    class Builder{
        fun build(beacon: Beacon?): BeaconInfo {
            val beaconInfo = BeaconInfo()
            beacon?.let { currentBeacon ->
                beaconInfo.name = currentBeacon.name
                beaconInfo.address = currentBeacon.address
                beaconInfo.connectible = currentBeacon.connectible
                beaconInfo.rssi = currentBeacon.rssi
                beaconInfo.manufacturer = currentBeacon.manufacturer
                beaconInfo.model = currentBeacon.model
                beaconInfo.softwareVersion = currentBeacon.softwareVersion
                beaconInfo.hardwareVersion = currentBeacon.hardwareVersion
                beaconInfo.firmwareVersion = currentBeacon.firmwareVersion
                beaconInfo.advFeature = currentBeacon.advFeature
                beaconInfo.supportedTxPower = currentBeacon.supportedTxPower.joinToString(", ")
                beaconInfo.supportedSlots = currentBeacon.supportedSlots.joinToString(", "){it.name}
                beaconInfo.slotAmount = currentBeacon.slotAmount
            }
            return beaconInfo
            
        }
    }


}