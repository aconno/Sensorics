package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.restpublish.RestHeaderEntity
import com.aconno.sensorics.domain.ifttt.GeneralRestHeader
import com.aconno.sensorics.domain.ifttt.RestHeader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestHeaderDataMapper @Inject constructor() {

    fun toRESTHeaderEntity(restHeader: RestHeader): RestHeaderEntity {
        return RestHeaderEntity(
            restHeader.id,
            restHeader.rId,
            restHeader.key,
            restHeader.value
        )
    }

    fun toRESTHeaderEntityList(restPublishEntityCollection: Collection<RestHeader>): List<RestHeaderEntity> {
        val restPublishList = mutableListOf<RestHeaderEntity>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHeaderEntity(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

    fun toRESTHeader(restHeaderEntity: RestHeaderEntity): RestHeader {
        return GeneralRestHeader(
            restHeaderEntity.id,
            restHeaderEntity.rId,
            restHeaderEntity.key,
            restHeaderEntity.value
        )
    }

    fun toRESTHeaderList(restPublishEntityCollection: Collection<RestHeaderEntity>): List<RestHeader> {
        val restPublishList = mutableListOf<RestHeader>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHeader(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }
}