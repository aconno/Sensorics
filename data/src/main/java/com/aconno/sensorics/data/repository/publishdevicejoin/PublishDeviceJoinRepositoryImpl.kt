package com.aconno.sensorics.data.repository.publishdevicejoin

import com.aconno.sensorics.data.mapper.PublishDeviceJoinMapper
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

class PublishDeviceJoinRepositoryImpl(
    private val publishDeviceJoinDao: PublishDeviceJoinDao,
    private val deviceMapper: DeviceMapper,
    private val publishDeviceJoinMapper: PublishDeviceJoinMapper
) : PublishDeviceJoinRepository {
    override fun getDevicesThatConnectedWithGooglePublish(googlePublishId: Long): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesThatConnectedWithGooglePublish(googlePublishId)
            .map(deviceMapper::toDeviceList)
    }

    override fun getDevicesThatConnectedWithRestPublish(restPublishId: Long): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesThatConnectedWithRestPublish(restPublishId)
            .map(deviceMapper::toDeviceList)
    }

    override fun getDevicesThatConnectedWithMqttPublish(mqttPublishId: Long): List<Device>? {
        return publishDeviceJoinDao.getDevicesThatConnectedWithMqttPublish(mqttPublishId)
            ?.map(deviceMapper::toDevice)
    }

    override fun getDevicesThatConnectedWithAzureMqttPublish(azureMqttPublishId: Long): List<Device>? {
        return publishDeviceJoinDao.getDevicesThatConnectedWithAzureMqttPublish(azureMqttPublishId)
            ?.map(deviceMapper::toDevice)
    }

    override fun addGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin) {
        publishDeviceJoinDao.insertGoogle(
            publishDeviceJoinMapper.toGooglePublishDeviceJoinEntity(
                googlePublishDeviceJoin
            )
        )
    }

    override fun addRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin) {
        publishDeviceJoinDao.insertRest(
            publishDeviceJoinMapper.toRestPublishDeviceJoinEntity(
                restPublishDeviceJoin
            )
        )
    }

    override fun addMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin) {
        publishDeviceJoinDao.insertMqtt(
            publishDeviceJoinMapper.toMqttPublishDeviceJoinEntity(
                mqttPublishDeviceJoin
            )
        )
    }

    override fun addAzureMqttPublishDeviceJoin(azureMqttPublishDeviceJoin: AzureMqttPublishDeviceJoin) {
        publishDeviceJoinDao.insertAzureMqtt(
            publishDeviceJoinMapper.toAzureMqttPublishDeviceJoinEntity(
                azureMqttPublishDeviceJoin
            )
        )
    }

    override fun deleteGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishDeviceJoinMapper.toGooglePublishDeviceJoinEntity(
                googlePublishDeviceJoin
            )
        )
    }

    override fun deleteRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishDeviceJoinMapper.toRestPublishDeviceJoinEntity(
                restPublishDeviceJoin
            )
        )
    }

    override fun deleteMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishDeviceJoinMapper.toMqttPublishDeviceJoinEntity(
                mqttPublishDeviceJoin
            )
        )
    }

    override fun deleteAzureMqttPublishDeviceJoin(azureMqttPublishDeviceJoin: AzureMqttPublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishDeviceJoinMapper.toAzureMqttPublishDeviceJoinEntity(
                azureMqttPublishDeviceJoin
            )
        )
    }
}