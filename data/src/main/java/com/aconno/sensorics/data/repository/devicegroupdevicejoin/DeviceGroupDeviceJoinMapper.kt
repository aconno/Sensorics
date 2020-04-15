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
}