package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.mqttvirtualscanningsource.MqttVirtualScanningSourceEntity
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.GeneralMqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import javax.inject.Inject

class MqttVirtualScanningSourceDataMapper @Inject constructor() {
    fun toMqttVirtualScanningSource(mqttVirtualScanningSourceEntity: MqttVirtualScanningSourceEntity): MqttVirtualScanningSource {
        return GeneralMqttVirtualScanningSource(mqttVirtualScanningSourceEntity.id,
                mqttVirtualScanningSourceEntity.name,
                mqttVirtualScanningSourceEntity.enabled)
    }

    fun toMqttVirtualScanningSourceEntity(mqttVirtualScanningSource: MqttVirtualScanningSource): MqttVirtualScanningSourceEntity {
        return MqttVirtualScanningSourceEntity(
                mqttVirtualScanningSource.id,
                mqttVirtualScanningSource.name,
                mqttVirtualScanningSource.enabled
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