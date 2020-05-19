package com.aconno.sensorics.data.repository.devicegroups

import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class DeviceGroupRepositoryImpl(
    private val deviceGroupDao: DeviceGroupDao,
    private val deviceGroupMapper: DeviceGroupMapper
) : DeviceGroupRepository {

    override fun getAllDeviceGroups(): Single<List<DeviceGroup>> {
        return deviceGroupDao.getAll().map { entities ->
            entities.map { entity ->
                deviceGroupMapper.toDeviceGroup(entity)
            }
        }
    }

    override fun insertDeviceGroup(deviceGroup: DeviceGroup): Single<Long> {
        return Single.fromCallable {
            val entity = deviceGroupMapper.toDeviceGroupEntity(deviceGroup)
            deviceGroupDao.insert(entity)
        }
    }

    override fun updateDeviceGroup(deviceGroup: DeviceGroup): Completable {
        return Completable.fromAction {
            val entity = deviceGroupMapper.toDeviceGroupEntity(deviceGroup)
            deviceGroupDao.update(entity)
        }
    }

    override fun deleteDeviceGroup(deviceGroup: DeviceGroup): Completable {
        return Completable.fromAction {
            val entity = deviceGroupMapper.toDeviceGroupEntity(deviceGroup)
            deviceGroupDao.delete(entity)
        }
    }

    override fun getDeviceGroupForDevice(deviceMacAddress: String): Maybe<DeviceGroup> {
        return deviceGroupDao.getDeviceGroupForDevice(deviceMacAddress)
                .map(deviceGroupMapper::toDeviceGroup)
    }

}