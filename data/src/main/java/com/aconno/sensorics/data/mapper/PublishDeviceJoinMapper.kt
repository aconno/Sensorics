package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.publishdevicejoin.GenericPublishDeviceJoinEntity
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublishDeviceJoinMapper @Inject constructor() {
    fun toPublishDeviceJoinEntity(publishDeviceJoin: PublishDeviceJoin): GenericPublishDeviceJoinEntity {
        return GenericPublishDeviceJoinEntity(
            publishDeviceJoin.publishId,
            publishDeviceJoin.deviceId,
            publishDeviceJoin.publishType.type
        )
    }
}