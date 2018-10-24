package com.aconno.sensorics.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface RestPublishRepository {
    fun addRESTPublish(restPublish: RestPublish): Long
    fun addRESTHeader(restHeader: List<RestHeader>)
    fun addHttpGetParams(restHttpGetParams: List<RestHttpGetParam>)
    fun updateRESTPublish(restPublish: RestPublish)
    fun deleteRESTPublish(restPublish: RestPublish)
    fun deleteRESTHeader(restHeader: RestHeader)
    fun deleteRESTHttpGetParam(restHttpGetParam: RestHttpGetParam)
    fun getAllRESTPublish(): Single<List<BasePublish>>
    fun getAllEnabledRESTPublish(): Single<List<BasePublish>>
    fun getRESTPublishById(RESTPublishId: Long): Maybe<RestPublish>
    fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeader>>
    fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParam>>
}