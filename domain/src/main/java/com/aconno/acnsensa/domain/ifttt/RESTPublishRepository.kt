package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Single

interface RESTPublishRepository {
    fun addRESTPublish(restPublish: RESTPublish)
    fun updateRESTPublish(restPublish: RESTPublish)
    fun deleteRESTPublish(restPublish: RESTPublish)
    fun getAllRESTPublish(): Single<List<BasePublish>>
    fun getAllEnabledRESTPublish(): Single<List<BasePublish>>
    fun getRESTPublishById(RESTPublishId: Long): Single<RESTPublish>
}