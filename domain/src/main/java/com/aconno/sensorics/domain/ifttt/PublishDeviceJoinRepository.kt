package com.aconno.sensorics.domain.ifttt

import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

interface PublishDeviceJoinRepository {
    fun getDevicesConnectedWithPublish(publishId: Long, publishType: String): Maybe<List<Device>>

    fun addPublishDeviceJoin(publishDeviceJoin: PublishDeviceJoin)
    fun deletePublishDeviceJoin(publishDeviceJoin: PublishDeviceJoin)
}