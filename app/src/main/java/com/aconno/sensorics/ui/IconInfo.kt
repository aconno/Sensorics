package com.aconno.sensorics.ui

import com.aconno.sensorics.model.DeviceActive

interface IconInfo {

    fun getIconInfo(deviceNames: List<DeviceActive>): HashMap<String, String>
}