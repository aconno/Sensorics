package com.aconno.sensorics.data.repository.devicegroups

import com.aconno.sensorics.domain.model.DeviceGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceGroupMapper @Inject constructor() {

    fun toDeviceGroup(deviceGroupEntity: DeviceGroupEntity): DeviceGroup {
        return DeviceGroup(
            deviceGroupEntity.id,
            deviceGroupEntity.name
        )
    }

    fun toDeviceGroupList(deviceGroupEntityList: Collection<DeviceGroupEntity>): List<DeviceGroup> {
        val list = mutableListOf<DeviceGroup>()
        deviceGroupEntityList.forEach {
            list.add(toDeviceGroup(it))
        }
        return list
    }

    fun toDeviceGroupEntity(deviceGroup: DeviceGroup): DeviceGroupEntity {
        return DeviceGroupEntity(
            deviceGroup.id,
            deviceGroup.groupName
        )
    }
}