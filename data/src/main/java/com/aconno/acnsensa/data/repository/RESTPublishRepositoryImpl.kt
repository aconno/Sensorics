package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.data.mapper.RESTPublishDataMapper
import com.aconno.acnsensa.data.mapper.RESTPublishEntityDataMapper
import com.aconno.acnsensa.domain.ifttt.*
import io.reactivex.Maybe
import io.reactivex.Single

class RESTPublishRepositoryImpl(
    private val restPublishDao: RESTPublishDao,
    private val restPublishEntityDataMapper: RESTPublishEntityDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper
) :
    RESTPublishRepository {
    override fun addRESTPublish(restPublish: RESTPublish): Long {
        return restPublishDao.insert(restPublishDataMapper.transform(restPublish))
    }

    override fun updateRESTPublish(restPublish: RESTPublish) {
        restPublishDao.update(restPublishDataMapper.transform(restPublish))
    }

    override fun deleteRESTPublish(restPublish: RESTPublish) {
        restPublishDao.delete(restPublishDataMapper.transform(restPublish))
    }

    override fun getAllRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.all.map(restPublishEntityDataMapper::transform)
    }

    override fun getAllEnabledRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.getEnabledRESTPublish()
            .map(restPublishEntityDataMapper::transform)
    }

    override fun getRESTPublishById(RESTPublishId: Long): Maybe<RESTPublish> {
        return restPublishDao.getRESTPublishById(RESTPublishId)
            .map(restPublishEntityDataMapper::transform)
    }
}
