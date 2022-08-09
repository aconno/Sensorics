package com.aconno.sensorics.data.repository.mqttvirtualscanningsource

import com.aconno.sensorics.data.mapper.MqttVirtualScanningSourceDataMapper
import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import io.reactivex.Maybe
import io.reactivex.Single

class MqttVirtualScanningSourceRepositoryImpl(
        private val mqttVirtualScanningSourceDao: MqttVirtualScanningSourceDao,
        private val mqttVirtualScanningSourceDataMapper: MqttVirtualScanningSourceDataMapper
) : MqttVirtualScanningSourceRepository {
    override fun addMqttVirtualScanningSource(mqttVirtualScanningSource: MqttVirtualScanningSource): Long {
        return mqttVirtualScanningSourceDao.insert(
                mqttVirtualScanningSourceDataMapper.toMqttVirtualScanningSourceEntity(mqttVirtualScanningSource)
        )
    }

    override fun deleteMqttVirtualScanningSource(mqttVirtualScanningSource: MqttVirtualScanningSource) {
        return mqttVirtualScanningSourceDao.delete(
                mqttVirtualScanningSourceDataMapper.toMqttVirtualScanningSourceEntity(mqttVirtualScanningSource)
        )
    }

    override fun getAllEnabledMqttVirtualScanningSource(): List<MqttVirtualScanningSource> {
        val enabledSources = mqttVirtualScanningSourceDao.getEnabledMqttVirtualScanningSource()
        return mqttVirtualScanningSourceDataMapper.toMqttVirtualScanningSourceList(enabledSources)
    }

    override fun getAllMqttVirtualScanningSource(): Single<List<BaseVirtualScanningSource>> {
        return mqttVirtualScanningSourceDao.all.map(
                mqttVirtualScanningSourceDataMapper::toMqttVirtualScanningSourceList
        )
    }

    override fun getMqttVirtualScanningSourceById(mqttVirtualScanningSourceId: Long): Maybe<MqttVirtualScanningSource> {
        return mqttVirtualScanningSourceDao.getMqttVirtualScanningSourceById(mqttVirtualScanningSourceId)
                .map(mqttVirtualScanningSourceDataMapper::toMqttVirtualScanningSource)
    }

    override fun updateMqttVirtualScanningSource(mqttVirtualScanningSource: MqttVirtualScanningSource) {
        mqttVirtualScanningSourceDao.update(
                mqttVirtualScanningSourceDataMapper.toMqttVirtualScanningSourceEntity(mqttVirtualScanningSource)
        )
    }
}