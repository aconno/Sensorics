package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.pdjoin.GooglePublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.pdjoin.MqttPublishDeviceJoinEntity
import com.aconno.sensorics.data.repository.pdjoin.RestPublishDeviceJoinEntity
import com.aconno.sensorics.domain.ifttt.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublishPublishDeviceJoinJoinMapper @Inject constructor() {

    fun toGooglePublishDeviceJoin(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity): GooglePublishDeviceJoin {
        return GeneralGooglePublishDeviceJoin(
            googlePublishDeviceJoinEntity.gId,
            googlePublishDeviceJoinEntity.dId
        )
    }

    fun toRestPublishDeviceJoin(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity): GeneralRestPublishDeviceJoin {
        return GeneralRestPublishDeviceJoin(
            restPublishDeviceJoinEntity.rId,
            restPublishDeviceJoinEntity.dId
        )
    }

    fun toMqttPublishDeviceJoin(mqttPublishDeviceJoinEntity: MqttPublishDeviceJoinEntity): GeneralMqttPublishDeviceJoin {
        return GeneralMqttPublishDeviceJoin(
            mqttPublishDeviceJoinEntity.mId,
            mqttPublishDeviceJoinEntity.dId
        )
    }


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