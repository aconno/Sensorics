package com.aconno.acnsensa.data.mapper

import com.aconno.acnsensa.data.repository.GooglePublishDeviceJoinEntity
import com.aconno.acnsensa.data.repository.RestPublishDeviceJoinEntity
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.RestPublishDeviceJoin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublishPublishDeviceJoinJoinMapper @Inject constructor() {

    fun toGooglePublishDeviceJoin(restPublishDeviceJoinEntity: RestPublishDeviceJoinEntity): GeneralRestPublishDeviceJoin {
        return GeneralRestPublishDeviceJoin(
            restPublishDeviceJoinEntity.rId,
            restPublishDeviceJoinEntity.dId
        )
    }

    fun toRestPublishDeviceJoin(googlePublishDeviceJoinEntity: GooglePublishDeviceJoinEntity): GooglePublishDeviceJoin {
        return GeneralGooglePublishDeviceJoin(
            googlePublishDeviceJoinEntity.gId,
            googlePublishDeviceJoinEntity.dId
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
}