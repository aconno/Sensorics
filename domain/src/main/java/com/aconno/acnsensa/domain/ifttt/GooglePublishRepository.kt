package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface GooglePublishRepository {
    fun addGooglePublish(googlePublish: GooglePublish): Long
    fun updateGooglePublish(googlePublish: GooglePublish)
    fun deleteGooglePublish(googlePublish: GooglePublish)
    fun getAllGooglePublish(): Single<List<BasePublish>>
    fun getAllEnabledGooglePublish(): Single<List<BasePublish>>
    fun getGooglePublishById(googlePublishId: Long): Maybe<GooglePublish>
}