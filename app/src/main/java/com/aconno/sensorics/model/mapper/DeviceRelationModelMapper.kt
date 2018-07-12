package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceRelationModel
import javax.inject.Inject

class DeviceRelationModelMapper @Inject constructor() {

    fun toDeviceRelationModel(device: Device, related: Boolean = false): DeviceRelationModel {
        return DeviceRelationModel(
            device.name,
            device.alias,
            device.macAddress,
            device.icon,
            related
        )
    }
}