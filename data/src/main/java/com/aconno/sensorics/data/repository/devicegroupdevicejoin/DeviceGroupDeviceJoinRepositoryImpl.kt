package com.aconno.sensorics.data.repository.devicegroupdevicejoin

import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.DeviceGroupDeviceJoin
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import io.reactivex.Maybe

class DeviceGroupDeviceJoinRepositoryImpl(
    private val deviceGroupDeviceJoinDao: DeviceGroupDeviceJoinDao,
    private val deviceMapper: DeviceMapper,
    private val deviceGroupDeviceJoinMapper: DeviceGroupDeviceJoinMapper
) : DeviceGroupDeviceJoinRepository {

    override fun getDevicesInDeviceGroup(deviceGroupId: Long): Maybe<List<Device>> {
        return deviceGroupDeviceJoinDao.getDevicesInDeviceGroup(deviceGroupId)
            .map(deviceMapper::toDeviceList)
    }

    override fun addDeviceGroupDeviceJoin(deviceGroupDeviceJoin: DeviceGroupDeviceJoin) {
        deviceGroupDeviceJoinDao.insert(
            deviceGroupDeviceJoinMapper.toDeviceGroupDeviceJoinEntity(
                deviceGroupDeviceJoin
            )
        )
    }

    override fun deleteDeviceGroupDeviceJoin(deviceGroupDeviceJoin: DeviceGroupDeviceJoin) {
        deviceGroupDeviceJoinDao.delete(
            deviceGroupDeviceJoinMapper.toDeviceGroupDeviceJoinEntity(
                deviceGroupDeviceJoin
            )
        )
    }

}