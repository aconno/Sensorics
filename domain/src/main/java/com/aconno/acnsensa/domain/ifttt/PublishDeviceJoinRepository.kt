package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Maybe

interface PublishDeviceJoinRepository {
    fun getDevicesThatConnectedWithGooglePublish(googlePublishId: Long): Maybe<List<Device>>
    fun getDevicesThatConnectedWithRestPublish(restPublishId: Long): Maybe<List<Device>>

    fun addGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin)
    fun addRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin)
    fun deleteGooglePublishDeviceJoin(googlePublishDeviceJoin: GooglePublishDeviceJoin)
    fun deleteRestPublishDeviceJoin(restPublishDeviceJoin: RestPublishDeviceJoin)
}