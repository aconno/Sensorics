package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.DeviceGroupDeviceJoin
import io.reactivex.Maybe

interface DeviceGroupDeviceJoinRepository {
    fun getDevicesInDeviceGroup(deviceGroupId: Long): Maybe<List<Device>>
    fun addDeviceGroupDeviceJoin(deviceGroupDeviceJoin: DeviceGroupDeviceJoin)
    fun deleteDeviceGroupDeviceJoin(deviceGroupDeviceJoin: DeviceGroupDeviceJoin)
    fun getDevices(): Maybe<List<Device>>
}