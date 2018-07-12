package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.GeneralRESTHeader
import com.aconno.sensorics.domain.ifttt.RESTHeader
import com.aconno.sensorics.model.RESTHeaderModel
import javax.inject.Inject

class RESTHeaderModelMapper @Inject constructor() {

    private fun toRESTHeaderModel(restHeader: RESTHeader): RESTHeaderModel {
        return RESTHeaderModel(
            restHeader.id,
            restHeader.rId,
            restHeader.key,
            restHeader.value
        )
    }

    fun toRESTHeaderModelList(googlePublishCollection: Collection<RESTHeader>): List<RESTHeaderModel> {
        val restHeaderModelList = mutableListOf<RESTHeaderModel>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeaderModel(restHeader)
            restHeaderModelList.add(user)
        }
        return restHeaderModelList
    }

    private fun toRESTHeader(restHeaderModel: RESTHeaderModel): RESTHeader {
        return GeneralRESTHeader(
            restHeaderModel.id,
            restHeaderModel.rId,
            restHeaderModel.key,
            restHeaderModel.value
        )
    }

    private fun toRESTHeaderByRESTPublishId(restHeaderModel: RESTHeaderModel, rId: Long): RESTHeader {
        return GeneralRESTHeader(
            restHeaderModel.id,
            rId,
            restHeaderModel.key,
            restHeaderModel.value
        )
    }

    fun toRESTHeaderListByRESTPublishId(
        googlePublishCollection: Collection<RESTHeaderModel>,
        rId: Long
    ): List<RESTHeader> {
        val restHeaderList = mutableListOf<RESTHeader>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeaderByRESTPublishId(restHeader, rId)
            restHeaderList.add(user)
        }
        return restHeaderList
    }

    fun toRESTHeaderList(googlePublishCollection: Collection<RESTHeaderModel>): List<RESTHeader> {
        val restHeaderList = mutableListOf<RESTHeader>()
        for (restHeader in googlePublishCollection) {
            val user = toRESTHeader(restHeader)
            restHeaderList.add(user)
        }
        return restHeaderList
    }

}