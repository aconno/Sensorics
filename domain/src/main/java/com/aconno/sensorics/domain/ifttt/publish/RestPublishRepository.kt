package com.aconno.sensorics.domain.ifttt.publish

import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestPublish
import io.reactivex.Maybe

interface RestPublishRepository : PublishRepository<RestPublish> {
    fun addRESTHeader(restHeader: List<RestHeader>)
    fun addHttpGetParams(restHttpGetParams: List<RestHttpGetParam>)
    fun deleteRESTHeader(restHeader: RestHeader)
    fun deleteRESTHttpGetParam(restHttpGetParam: RestHttpGetParam)
    fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeader>>
    fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParam>>
}