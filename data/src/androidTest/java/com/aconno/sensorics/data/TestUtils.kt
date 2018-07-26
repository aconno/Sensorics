package com.aconno.sensorics.data

import com.aconno.sensorics.domain.model.Device

object TestUtils {

    fun getDevice(
        name: String = "Test",
        alias: String = "Test",
        macAddress: String = "AB:CD:EF:12:34:56",
        icon: String = "ic_test"
    ): Device {
        return Device(name, alias, macAddress, icon)
    }
}