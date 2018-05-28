package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.ifttt.*
import io.reactivex.Single

class RESTPublishRepositoryImpl(private val restPublishDao: RESTPublishDao) :
    RESTPublishRepository {
    override fun addRESTPublish(restPublish: RESTPublish) {
        restPublishDao.insert(toEntity(restPublish))
    }

    override fun updateRESTPublish(restPublish: RESTPublish) {
        restPublishDao.update(toEntity(restPublish))
    }

    override fun deleteRESTPublish(restPublish: RESTPublish) {
        restPublishDao.delete(toEntity(restPublish))
    }

    override fun getAllRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.all.map { actionEntities -> actionEntities.map { toRESTPublish(it) } }
    }

    override fun getAllEnabledRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.getEnabledRESTPublish()
            .map { actionEntities -> actionEntities.map { toRESTPublish(it) } }
    }

    override fun getRESTPublishById(RESTPublishId: Long): Single<RESTPublish> {
        return restPublishDao.getRESTPublishById(RESTPublishId)
            .map { actionEntity -> toRESTPublish(actionEntity) }
    }

    private fun toEntity(restPublish: RESTPublish): RESTPublishEntity {
        return RESTPublishEntity(
            restPublish.id,
            restPublish.name,
            restPublish.url,
            restPublish.method,
            restPublish.enabled
        )
    }

    private fun toRESTPublish(restPublishEntity: RESTPublishEntity): RESTPublish {
        return GeneralRESTPublish(
            restPublishEntity.id,
            restPublishEntity.name,
            restPublishEntity.url,
            restPublishEntity.method,
            restPublishEntity.enabled
        )
    }
}