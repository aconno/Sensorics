package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import io.reactivex.Single

class GooglePublishRepositoryImpl(private val googlePublishDao: GooglePublishDao) :
    GooglePublishRepository {
    override fun addGooglePublish(googlePublish: GooglePublish) {
        googlePublishDao.insert(toEntity(googlePublish))
    }

    override fun updateGooglePublish(googlePublish: GooglePublish) {
        googlePublishDao.update(toEntity(googlePublish))
    }

    override fun deleteGooglePublish(googlePublish: GooglePublish) {
        googlePublishDao.delete(toEntity(googlePublish))
    }

    override fun getAllGooglePublish(): Single<List<BasePublish>> {
        return googlePublishDao.all.map { actionEntities -> actionEntities.map { toGooglePublish(it) } }
    }

    override fun getGooglePublishById(googlePublishId: Long): Single<GooglePublish> {
        return googlePublishDao.getGooglePublishById(googlePublishId)
            .map { actionEntity -> toGooglePublish(actionEntity) }
    }

    override fun getAllEnabledGooglePublish(): Single<List<BasePublish>> {
        return googlePublishDao.getEnabledGooglePublish()
            .map { actionEntities -> actionEntities.map { toGooglePublish(it) } }
    }

    private fun toEntity(googlePublish: GooglePublish): GooglePublishEntity {
        return GooglePublishEntity(
            googlePublish.id,
            googlePublish.name,
            googlePublish.projectId,
            googlePublish.region,
            googlePublish.deviceRegistry,
            googlePublish.device,
            googlePublish.privateKey,
            googlePublish.enabled
        )
    }

    private fun toGooglePublish(googlePublishEntity: GooglePublishEntity): GooglePublish {
        return GeneralGooglePublish(
            googlePublishEntity.id,
            googlePublishEntity.name,
            googlePublishEntity.projectId,
            googlePublishEntity.region,
            googlePublishEntity.deviceRegistry,
            googlePublishEntity.device,
            googlePublishEntity.privateKey,
            googlePublishEntity.enabled
        )
    }
}