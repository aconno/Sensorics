package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceEntity
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.MqttVirtualScanningSourceProtocol
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.GeneralMqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import javax.inject.Inject

class MqttVirtualScanningSourceDataMapper @Inject constructor() {
    fun toMqttVirtualScanningSource(mqttVirtualScanningSourceEntity: MqttVirtualScanningSourceEntity): MqttVirtualScanningSource {
        return GeneralMqttVirtualScanningSource(mqttVirtualScanningSourceEntity.id,
                mqttVirtualScanningSourceEntity.name,
                mqttVirtualScanningSourceEntity.enabled,
                MqttVirtualScanningSourceProtocol.valueOf(mqttVirtualScanningSourceEntity.protocol),
                mqttVirtualScanningSourceEntity.address,
                mqttVirtualScanningSourceEntity.port,
                mqttVirtualScanningSourceEntity.path,
                mqttVirtualScanningSourceEntity.clientId,
                mqttVirtualScanningSourceEntity.username,
                mqttVirtualScanningSourceEntity.password,
                mqttVirtualScanningSourceEntity.qualityOfService
                )
    }

    fun toMqttVirtualScanningSourceEntity(mqttVirtualScanningSource: MqttVirtualScanningSource): MqttVirtualScanningSourceEntity {
        return MqttVirtualScanningSourceEntity(
                mqttVirtualScanningSource.id,
                mqttVirtualScanningSource.name,
                mqttVirtualScanningSource.enabled,
                mqttVirtualScanningSource.protocol.name,
                mqttVirtualScanningSource.address,
                mqttVirtualScanningSource.port,
                mqttVirtualScanningSource.path,
                mqttVirtualScanningSource.clientId,
                mqttVirtualScanningSource.username,
                mqttVirtualScanningSource.password,
                mqttVirtualScanningSource.qualityOfService
        )
    }

    fun toMqttVirtualScanningSourceList(mqttVirtualScanningSourceEntities: List<MqttVirtualScanningSourceEntity>): List<MqttVirtualScanningSource> {
        val mqttSourceList = mutableListOf<MqttVirtualScanningSource>()
        for (mqttVirtualScanningSourceEntity in mqttVirtualScanningSourceEntities) {
            val source = toMqttVirtualScanningSource(mqttVirtualScanningSourceEntity)
            mqttSourceList.add(source)
        }
        return mqttSourceList
    }
}