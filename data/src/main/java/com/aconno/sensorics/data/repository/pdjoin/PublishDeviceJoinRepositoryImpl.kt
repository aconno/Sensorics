package com.aconno.sensorics.data.repository.pdjoin

import com.aconno.sensorics.data.mapper.PublishPublishDeviceJoinJoinMapper
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.domain.ifttt.GooglePublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.MqttPublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.ifttt.RestPublishDeviceJoin
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

class PublishDeviceJoinRepositoryImpl(
    private val publishDeviceJoinDao: PublishDeviceJoinDao,
    private val deviceMapper: DeviceMapper,
    private val publishPublishDeviceJoinJoinMapper: PublishPublishDeviceJoinJoinMapper
) : PublishDeviceJoinRepository {
    override fun getDevicesThatConnectedWithGooglePublish(googlePublishId: Long): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesThatConnectedWithGooglePublish(googlePublishId)
            .map(deviceMapper::toDeviceList)
    }

    override fun getDevicesThatConnectedWithRestPublish(restPublishId: Long): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesThatConnectedWithRestPublish(restPublishId)
            .map(deviceMapper::toDeviceList)
    }

    override fun getDevicesThatConnectedWithMqttPublish(mqttPublishId: Long): Maybe<List<Device>> {
        return publishDeviceJoinDao.getDevicesThatConnectedWithMqttPublish(mqttPublishId)
            .map(deviceMapper::toDeviceList)
    }

    override fun addGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin) {
        publishDeviceJoinDao.insertGoogle(
            publishPublishDeviceJoinJoinMapper.toGooglePublishDeviceJoinEntity(
                googlePublishDeviceJoin
            )
        )
    }

    override fun addRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin) {
        publishDeviceJoinDao.insertRest(
            publishPublishDeviceJoinJoinMapper.toRestPublishDeviceJoinEntity(
                restPublishDeviceJoin
            )
        )
    }

    override fun addMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin) {
        publishDeviceJoinDao.insertMqtt(
            publishPublishDeviceJoinJoinMapper.toMqttPublishDeviceJoinEntity(
                mqttPublishDeviceJoin
            )
        )
    }

    override fun deleteGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishPublishDeviceJoinJoinMapper.toGooglePublishDeviceJoinEntity(
                googlePublishDeviceJoin
            )
        )
    }

    override fun deleteRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishPublishDeviceJoinJoinMapper.toRestPublishDeviceJoinEntity(
                restPublishDeviceJoin
            )
        )
    }

    override fun deleteMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin) {
        publishDeviceJoinDao.delete(
            publishPublishDeviceJoinJoinMapper.toMqttPublishDeviceJoinEntity(
                mqttPublishDeviceJoin
            )
        )
    }
}