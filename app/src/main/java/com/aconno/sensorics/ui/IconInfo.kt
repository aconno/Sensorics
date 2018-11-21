package com.aconno.sensorics.ui

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceActive

interface IconInfo {

    fun getIconInfoForActiveDevices(deviceNames: List<DeviceActive>): HashMap<String, String>
    fun getIconInfoForDevices(deviceNames: List<Device>): HashMap<String, String>
}