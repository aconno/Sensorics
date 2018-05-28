package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Single

interface GooglePublishRepository {
    fun addGooglePublish(googlePublish: GooglePublish)
    fun updateGooglePublish(googlePublish: GooglePublish)
    fun deleteGooglePublish(googlePublish: GooglePublish)
    fun getAllGooglePublish(): Single<List<BasePublish>>
    fun getAllEnabledGooglePublish(): Single<List<BasePublish>>
    fun getGooglePublishById(googlePublishId: Long): Single<GooglePublish>
}