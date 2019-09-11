package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

interface PublishDeviceJoinRepository {
    fun getDevicesThatConnectedWithGooglePublish(googlePublishId: Long): Maybe<List<Device>>
    fun getDevicesThatConnectedWithRestPublish(restPublishId: Long): Maybe<List<Device>>
    fun getDevicesThatConnectedWithMqttPublish(mqttPublishId: Long): Maybe<List<Device>>

    fun addGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin)
    fun addRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin)
    fun addMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin)

    fun deleteGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin)
    fun deleteRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin)
    fun deleteMqttPublishDeviceJoin(mqttPublishDeviceJoin: MqttPublishDeviceJoin)
}