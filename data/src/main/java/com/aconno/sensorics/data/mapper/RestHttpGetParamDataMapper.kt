package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.restpublish.RestHttpGetParamEntity
import com.aconno.sensorics.domain.ifttt.GeneralRestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestHttpGetParamDataMapper @Inject constructor() {

    fun toRESTHttpGetParamEntity(restHttpGetParam: RestHttpGetParam): RestHttpGetParamEntity {
        return RestHttpGetParamEntity(
            restHttpGetParam.id,
            restHttpGetParam.rId,
            restHttpGetParam.key,
            restHttpGetParam.value
        )
    }

    fun toRESTHttpGetParamEntityList(restPublishEntityCollection: Collection<RestHttpGetParam>): List<RestHttpGetParamEntity> {
        val restPublishList = mutableListOf<RestHttpGetParamEntity>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHttpGetParamEntity(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

    fun toRESTHttpGetParam(restHttpGetParamEntity: RestHttpGetParamEntity): RestHttpGetParam {
        return GeneralRestHttpGetParam(
            restHttpGetParamEntity.id,
            restHttpGetParamEntity.rId,
            restHttpGetParamEntity.key,
            restHttpGetParamEntity.value
        )
    }

    fun toRESTHttpGetParamList(restPublishEntityCollection: Collection<RestHttpGetParamEntity>): List<RestHttpGetParam> {
        val restPublishList = mutableListOf<RestHttpGetParam>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHttpGetParam(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

}