package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.data.mapper.GooglePublishDataMapper
import com.aconno.acnsensa.data.mapper.GooglePublishEntityDataMapper
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import io.reactivex.Maybe
import io.reactivex.Single

class GooglePublishRepositoryImpl(
    private val googlePublishDao: GooglePublishDao,
    private val googlePublishEntityDataMapper: GooglePublishEntityDataMapper,
    private val googlePublishDataMapper: GooglePublishDataMapper
) :
    GooglePublishRepository {
    override fun addGooglePublish(googlePublish: GooglePublish): Long {
        return googlePublishDao.insert(googlePublishDataMapper.transform(googlePublish))
    }

    override fun updateGooglePublish(googlePublish: GooglePublish) {
        googlePublishDao.update(googlePublishDataMapper.transform(googlePublish))
    }

    override fun deleteGooglePublish(googlePublish: GooglePublish) {
        googlePublishDao.delete(googlePublishDataMapper.transform(googlePublish))
    }

    override fun getAllGooglePublish(): Single<List<BasePublish>> {
        return googlePublishDao.all.map(googlePublishEntityDataMapper::transform)
    }

    override fun getGooglePublishById(googlePublishId: Long): Maybe<GooglePublish> {
        return googlePublishDao.getGooglePublishById(googlePublishId)
            .map(googlePublishEntityDataMapper::transform)
    }

    override fun getAllEnabledGooglePublish(): Single<List<BasePublish>> {
        return googlePublishDao.getEnabledGooglePublish()
            .map(googlePublishEntityDataMapper::transform)
    }
}