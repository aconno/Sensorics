package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.model.RestHttpGetParamModel
import javax.inject.Inject

class RESTHttpGetParamModelMapper @Inject constructor() {

    private fun toRESTHttpGetParamModel(restHttpGetParam: RestHttpGetParam): RestHttpGetParamModel {
        return RestHttpGetParamModel(
            restHttpGetParam.id,
            restHttpGetParam.rId,
            restHttpGetParam.key,
            restHttpGetParam.value
        )
    }

    fun toRESTHttpGetParamModelList(collection: Collection<RestHttpGetParam>): List<RestHttpGetParamModel> {
        val restHttpGetParamModelList = mutableListOf<RestHttpGetParamModel>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParamModel(restHttpGetParam)
            restHttpGetParamModelList.add(user)
        }
        return restHttpGetParamModelList
    }

    private fun toRESTHttpGetParam(restHttpGetParamModel: RestHttpGetParamModel): RestHttpGetParam {
        return GeneralRestHttpGetParam(
            restHttpGetParamModel.id,
            restHttpGetParamModel.rId,
            restHttpGetParamModel.key,
            restHttpGetParamModel.value
        )
    }

    private fun toRESTHttpGetParamByRESTPublishId(
        restHttpGetParamModel: RestHttpGetParamModel,
        rId: Long
    ): RestHttpGetParam {
        return GeneralRestHttpGetParam(
            restHttpGetParamModel.id,
            rId,
            restHttpGetParamModel.key,
            restHttpGetParamModel.value
        )
    }

    fun toRESTHttpGetParamListByRESTPublishId(
        collection: Collection<RestHttpGetParamModel>,
        rId: Long
    ): List<RestHttpGetParam> {
        val restHttpGetParamList = mutableListOf<RestHttpGetParam>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParamByRESTPublishId(restHttpGetParam, rId)
            restHttpGetParamList.add(user)
        }
        return restHttpGetParamList
    }

    fun toRESTHttpGetParamList(collection: Collection<RestHttpGetParamModel>): List<RestHttpGetParam> {
        val restHttpGetParamList = mutableListOf<RestHttpGetParam>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParam(restHttpGetParam)
            restHttpGetParamList.add(user)
        }
        return restHttpGetParamList
    }

}