package com.aconno.sensorics.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface RESTPublishRepository {
    fun addRESTPublish(restPublish: RESTPublish): Long
    fun addRESTHeader(restHeader: List<RESTHeader>)
    fun addHttpGetParams(restHttpGetParams: List<RESTHttpGetParam>)
    fun updateRESTPublish(restPublish: RESTPublish)
    fun deleteRESTPublish(restPublish: RESTPublish)
    fun deleteRESTHeader(restHeader: RESTHeader)
    fun deleteRESTHttpGetParam(restHttpGetParam: RESTHttpGetParam)
    fun getAllRESTPublish(): Single<List<BasePublish>>
    fun getAllEnabledRESTPublish(): Single<List<BasePublish>>
    fun getRESTPublishById(RESTPublishId: Long): Maybe<RESTPublish>
    fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RESTHeader>>
    fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RESTHttpGetParam>>
}