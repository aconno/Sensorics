package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRestHeader
import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.model.RestHeaderModel
import javax.inject.Inject

class RESTHeaderModelMapper @Inject constructor() {

    private fun toRESTHeaderModel(restHeader: RestHeader): RestHeaderModel {
        return RestHeaderModel(
            restHeader.id,
            restHeader.rId,
            restHeader.key,
            restHeader.value
        )
    }

    fun toRESTHeaderModelList(googlePublishCollection: Collection<RestHeader>): List<RestHeaderModel> {
        val restHeaderModelList = mutableListOf<RestHeaderModel>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeaderModel(restHeader)
            restHeaderModelList.add(user)
        }
        return restHeaderModelList
    }

    private fun toRESTHeader(restHeaderModel: RestHeaderModel): RestHeader {
        return GeneralRestHeader(
            restHeaderModel.id,
            restHeaderModel.rId,
            restHeaderModel.key,
            restHeaderModel.value
        )
    }

    private fun toRESTHeaderByRESTPublishId(
        restHeaderModel: RestHeaderModel,
        rId: Long
    ): RestHeader {
        return GeneralRestHeader(
            restHeaderModel.id,
            rId,
            restHeaderModel.key,
            restHeaderModel.value
        )
    }

    fun toRESTHeaderListByRESTPublishId(
        googlePublishCollection: Collection<RestHeaderModel>,
        rId: Long
    ): List<RestHeader> {
        val restHeaderList = mutableListOf<RestHeader>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeaderByRESTPublishId(restHeader, rId)
            restHeaderList.add(user)
        }
        return restHeaderList
    }

    fun toRESTHeaderList(googlePublishCollection: Collection<RestHeaderModel>): List<RestHeader> {
        val restHeaderList = mutableListOf<RestHeader>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeader(restHeader)
            restHeaderList.add(user)
        }
        return restHeaderList
    }

}