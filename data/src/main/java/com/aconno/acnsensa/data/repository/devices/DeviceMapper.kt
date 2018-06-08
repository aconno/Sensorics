package com.aconno.acnsensa.data.repository.devices

import com.aconno.acnsensa.domain.model.Device
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceMapper @Inject constructor() {

    fun toDevice(deviceEntity: DeviceEntity): Device {
        return Device(
            deviceEntity.name,
            deviceEntity.macAddress
        )
    }

    fun toDeviceList(deviceEntityList: Collection<DeviceEntity>): List<Device> {
        val list = mutableListOf<Device>()
        deviceEntityList.forEach {
            list.add(toDevice(it))
        }
        return list
    }

    fun toDeviceEntity(device: Device): DeviceEntity {
        return DeviceEntity(
            device.name,
            device.macAddress
        )
    }
}