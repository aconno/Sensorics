package com.aconno.sensorics.data.repository.publishdevicejoin

import com.aconno.sensorics.data.mapper.PublishDeviceJoinMapper
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

class PublishDeviceJoinRepositoryImpl(
    private val publishDeviceJoinDao: PublishDeviceJoinDao,
    private val deviceMapper: DeviceMapper,
    private val publishDeviceJoinMapper: PublishDeviceJoinMapper
) : PublishDeviceJoinRepository {
    override fun getDevicesConnectedWithPublish(publishId: Long, publishType: String): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesConnectedWithPublish(publishId, publishType)
            .map(deviceMapper::toDeviceList)
    }

    override fun addPublishDeviceJoin(publishDeviceJoin: PublishDeviceJoin) {
        publishDeviceJoinDao.insert(
            publishDeviceJoinMapper.toPublishDeviceJoinEntity(
                publishDeviceJoin
            )
        )
    }

    override fun deletePublishDeviceJoin(publishDeviceJoin: PublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishDeviceJoinMapper.toPublishDeviceJoinEntity(
                publishDeviceJoin
            )
        )
    }
}