package com.aconno.sensorics.data.repository.devicegroupdevicejoin

import com.aconno.sensorics.domain.model.DeviceGroupDeviceJoin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceGroupDeviceJoinMapper @Inject constructor() {
    fun toDeviceGroupDeviceJoinEntity(deviceGroupDeviceJoin: DeviceGroupDeviceJoin): DeviceGroupDeviceJoinEntity {
        return DeviceGroupDeviceJoinEntity(
            deviceGroupDeviceJoin.deviceGroupId,
            deviceGroupDeviceJoin.deviceId
        )
    }

    fun toDeviceGroupDeviceJoin(deviceGroupDeviceJoinEntity: DeviceGroupDeviceJoinEntity): DeviceGroupDeviceJoin {
        return DeviceGroupDeviceJoin(
            deviceGroupDeviceJoinEntity.deviceGroupId,
            deviceGroupDeviceJoinEntity.deviceId
        )
    }

    fun toDeviceGroupDeviceJoinList(deviceGroupDeviceJoinEntityList: Collection<DeviceGroupDeviceJoinEntity>): List<DeviceGroupDeviceJoin> {
        val list = mutableListOf<DeviceGroupDeviceJoin>()
        deviceGroupDeviceJoinEntityList.forEach {
            list.add(toDeviceGroupDeviceJoin(it))
        }
        return list

    }
}