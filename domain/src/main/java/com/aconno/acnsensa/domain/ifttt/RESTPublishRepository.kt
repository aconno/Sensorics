package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface RESTPublishRepository {
    fun addRESTPublish(restPublish: RESTPublish): Long
    fun addRESTHeader(restHeader: List<RESTHeader>)
    fun updateRESTPublish(restPublish: RESTPublish)
    fun deleteRESTPublish(restPublish: RESTPublish)
    fun deleteRESTHeader(restHeader: RESTHeader)
    fun getAllRESTPublish(): Single<List<BasePublish>>
    fun getAllEnabledRESTPublish(): Single<List<BasePublish>>
    fun getRESTPublishById(RESTPublishId: Long): Maybe<RESTPublish>
    fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RESTHeader>>
}