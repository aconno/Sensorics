package com.aconno.sensorics.data.repository

import com.aconno.sensorics.data.mapper.PublishEntityMapper
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import io.reactivex.Maybe
import io.reactivex.Single


open class PublishRepositoryImpl<P, E>(
    private val dao: PublishDao<E>,
    private val mapper: PublishEntityMapper<P, E>
) : PublishRepository<P> where P : BasePublish, E : PublishEntity {
    override val all: Single<List<P>>
        get() = dao.all.map(mapper::fromEntities)

    override val allEnabled: Single<List<P>>
        get() = dao.allEnabled.map(mapper::fromEntities)

    override fun getPublishById(id: Long): Maybe<P> {
        return dao.getById(id).map(mapper::fromEntity)
    }

    override fun addPublish(publish: P): Long {
        return dao.insert(mapper.toEntity(publish))
    }

    override fun updatePublish(publish: P) {
        return dao.update(mapper.toEntity(publish))
    }

    override fun deletePublish(publish: P) {
        return dao.delete(mapper.toEntity(publish))
    }
}