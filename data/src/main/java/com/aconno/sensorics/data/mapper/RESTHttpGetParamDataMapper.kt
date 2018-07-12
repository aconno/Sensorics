package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.rpublish.RESTHttpGetParamEntity
import com.aconno.sensorics.domain.ifttt.GeneralRESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RESTHttpGetParamDataMapper @Inject constructor() {

    fun toRESTHttpGetParamEntity(restHttpGetParam: RESTHttpGetParam): RESTHttpGetParamEntity {
        return RESTHttpGetParamEntity(
            restHttpGetParam.id,
            restHttpGetParam.rId,
            restHttpGetParam.key,
            restHttpGetParam.value
        )
    }

    fun toRESTHttpGetParamEntityList(restPublishEntityCollection: Collection<RESTHttpGetParam>): List<RESTHttpGetParamEntity> {
        val restPublishList = mutableListOf<RESTHttpGetParamEntity>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHttpGetParamEntity(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

    fun toRESTHttpGetParam(restHttpGetParamEntity: RESTHttpGetParamEntity): RESTHttpGetParam {
        return GeneralRESTHttpGetParam(
            restHttpGetParamEntity.id,
            restHttpGetParamEntity.rId,
            restHttpGetParamEntity.key,
            restHttpGetParamEntity.value
        )
    }

    fun toRESTHttpGetParamList(restPublishEntityCollection: Collection<RESTHttpGetParamEntity>): List<RESTHttpGetParam> {
        val restPublishList = mutableListOf<RESTHttpGetParam>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = toRESTHttpGetParam(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }

}