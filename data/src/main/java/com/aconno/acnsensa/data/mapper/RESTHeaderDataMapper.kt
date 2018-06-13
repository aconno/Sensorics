package com.aconno.acnsensa.data.mapper

import com.aconno.acnsensa.data.repository.rpublish.RESTHeaderEntity
import com.aconno.acnsensa.data.repository.rpublish.RESTPublishEntity
import com.aconno.acnsensa.domain.ifttt.GeneralRESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RESTHeaderDataMapper @Inject constructor() {

    fun toRESTHeaderEntity(restHeader: RESTHeader): RESTHeaderEntity {
        return RESTHeaderEntity(
            restHeader.id,
            restHeader.rId,
            restHeader.key,
            restHeader.value
        )
    }

    fun toRESTHeaderEntityList(restPublishEntityCollection: Collection<RESTHeader>): List<RESTHeaderEntity> {
        val restPublishList = mutableListOf<RESTHeaderEntity>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHeaderEntity(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

    fun toRESTHeader(restHeaderEntity: RESTHeaderEntity): RESTHeader {
        return GeneralRESTHeader(
            restHeaderEntity.id,
            restHeaderEntity.rId,
            restHeaderEntity.key,
            restHeaderEntity.value
        )
    }

    fun toRESTHeaderList(restPublishEntityCollection: Collection<RESTHeaderEntity>): List<RESTHeader> {
        val restPublishList = mutableListOf<RESTHeader>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHeader(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }
}