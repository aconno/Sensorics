package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import com.aconno.sensorics.model.RESTHttpGetParamModel
import javax.inject.Inject

class RESTHttpGetParamModelMapper @Inject constructor() {

    fun toRESTHttpGetParamModel(restHttpGetParam: RESTHttpGetParam): RESTHttpGetParamModel {
        return RESTHttpGetParamModel(
            restHttpGetParam.id,
            restHttpGetParam.rId,
            restHttpGetParam.key,
            restHttpGetParam.value
        )
    }

    fun toRESTHttpGetParamModelList(collection: Collection<RESTHttpGetParam>): List<RESTHttpGetParamModel> {
        val restHttpGetParamModelList = mutableListOf<RESTHttpGetParamModel>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParamModel(restHttpGetParam)
            restHttpGetParamModelList.add(user)
        }
        return restHttpGetParamModelList
    }

    fun toRESTHttpGetParam(restHttpGetParamModel: RESTHttpGetParamModel): RESTHttpGetParam {
        return GeneralRESTHttpGetParam(
            restHttpGetParamModel.id,
            restHttpGetParamModel.rId,
            restHttpGetParamModel.key,
            restHttpGetParamModel.value
        )
    }

    fun toRESTHttpGetParamByRESTPublishId(
        restHttpGetParamModel: RESTHttpGetParamModel,
        rId: Long
    ): RESTHttpGetParam {
        return GeneralRESTHttpGetParam(
            restHttpGetParamModel.id,
            rId,
            restHttpGetParamModel.key,
            restHttpGetParamModel.value
        )
    }

    fun toRESTHttpGetParamListByRESTPublishId(
        collection: Collection<RESTHttpGetParamModel>,
        rId: Long
    ): List<RESTHttpGetParam> {
        val restHttpGetParamList = mutableListOf<RESTHttpGetParam>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParamByRESTPublishId(restHttpGetParam, rId)
            restHttpGetParamList.add(user)
        }
        return restHttpGetParamList
    }

    fun toRESTHttpGetParamList(collection: Collection<RESTHttpGetParamModel>): List<RESTHttpGetParam> {
        val restHttpGetParamList = mutableListOf<RESTHttpGetParam>()
        for (restHttpGetParam in collection) {
            val user = toRESTHttpGetParam(restHttpGetParam)
            restHttpGetParamList.add(user)
        }
        return restHttpGetParamList
    }

}