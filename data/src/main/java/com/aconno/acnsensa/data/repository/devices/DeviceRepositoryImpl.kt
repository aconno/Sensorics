package com.aconno.acnsensa.data.repository.devices

import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import io.reactivex.Completable
import io.reactivex.Single

class DeviceRepositoryImpl(
    private val deviceDao: DeviceDao,
    private val deviceMapper: DeviceMapper
) : DeviceRepository {

    override fun getAllDevices(): Single<List<Device>> {
        return deviceDao.getAll().map { entities ->
            entities.map { entity ->
                deviceMapper.toDevice(entity)
            }
        }
    }

    override fun insertDevice(device: Device): Completable {
        return Completable.fromAction {
            val entity = deviceMapper.toDeviceEntity(device)
            deviceDao.insert(entity)
        }
    }

    override fun deleteDevice(device: Device): Completable {
        return Completable.fromAction {
            val entity = deviceMapper.toDeviceEntity(device)
            deviceDao.delete(entity)
        }
    }
}