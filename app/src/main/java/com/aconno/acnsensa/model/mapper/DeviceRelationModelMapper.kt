package com.aconno.acnsensa.model.mapper

import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.DeviceRelationModel
import javax.inject.Inject

class DeviceRelationModelMapper @Inject constructor() {

    fun toDeviceRelationModel(device: Device, related: Boolean = false): DeviceRelationModel {
        return DeviceRelationModel(
            device.name,
            device.macAddress,
            related
        )
    }

    fun toDevice(deviceRelationModel: DeviceRelationModel): Device {
        return Device(
            deviceRelationModel.name,
            deviceRelationModel.macAddress
        )
    }
}