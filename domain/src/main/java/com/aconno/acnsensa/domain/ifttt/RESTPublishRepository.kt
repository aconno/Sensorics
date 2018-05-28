package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Single

interface RESTPublishRepository {
    fun addRESTPublish(restPublish: RESTPublish)
    fun updateRESTPublish(restPublish: RESTPublish)
    fun deleteRESTPublish(restPublish: RESTPublish)
    fun getAllRESTPublish(): Single<List<RESTPublish>>
    fun getAllEnabledRESTPublish(): Single<List<RESTPublish>>
    fun getRESTPublishById(RESTPublishId: Long): Single<RESTPublish>
}