package com.aconno.sensorics.data.repository.devices

import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class DeviceRepositoryImpl(
    private val deviceDao: DeviceDao,
    private val deviceMapper: DeviceMapper
) : DeviceRepository {

    override fun getAllDevices(): Flowable<List<Device>> {
        return deviceDao.getAll().map { entities ->
            entities.map { entity ->
                deviceMapper.toDevice(entity)
            }
        }
    }

    override fun getAllDevicesMaybe(): Maybe<List<Device>> {
        return deviceDao.getAllMaybe().map(deviceMapper::toDeviceList)
    }

    override fun insertDevice(device: Device): Completable {
        return Completable.fromAction {
            val entity = deviceMapper.toDeviceEntity(device)
            deviceDao.insert(entity)
        }
    }

    override fun updateDevice(device: Device): Completable {
        return Completable.fromAction {
            val entity = deviceMapper.toDeviceEntity(device)
            deviceDao.update(entity)
        }
    }

    override fun deleteDevice(device: Device): Completable {
        return Completable.fromAction {
            val entity = deviceMapper.toDeviceEntity(device)
            deviceDao.delete(entity)
        }
    }
}