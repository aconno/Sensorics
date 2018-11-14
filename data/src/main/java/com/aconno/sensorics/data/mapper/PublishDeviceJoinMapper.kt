package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.publishdevicejoin.GooglePublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.MqttPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.publishdevicejoin.RestPublishDeviceJoinEntity
import com.aconno.sensorics.domain.ifttt.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublishDeviceJoinMapper @Inject constructor() {

    fun toGooglePublishDeviceJoinEntity(publishDeviceJoin: GooglePublishDeviceJoin): GooglePublishDeviceJoinEntity {
        return GooglePublishDeviceJoinEntity(
            publishDeviceJoin.gId,
            publishDeviceJoin.dId
        )
    }

    fun toRestPublishDeviceJoinEntity(restPublishDeviceJoin: RestPublishDeviceJoin): RestPublishDeviceJoinEntity {
        return RestPublishDeviceJoinEntity(
            restPublishDeviceJoin.rId,
            restPublishDeviceJoin.dId
        )
    }

    fun toMqttPublishDeviceJoinEntity(mqttPublishDeviceJoin: MqttPublishDeviceJoin): MqttPublishDeviceJoinEntity {
        return MqttPublishDeviceJoinEntity(
            mqttPublishDeviceJoin.mId,
            mqttPublishDeviceJoin.dId
        )
    }
}