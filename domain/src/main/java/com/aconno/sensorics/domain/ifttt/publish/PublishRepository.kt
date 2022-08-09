package com.aconno.sensorics.domain.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import io.reactivex.Maybe
import io.reactivex.Single

interface PublishRepository<P> where P : BasePublish {
    val all: Single<List<P>>
    val allEnabled: Single<List<P>>
    fun getPublishById(id: Long): Maybe<P>
    fun addPublish(publish: P): Long
    fun updatePublish(publish: P)
    fun deletePublish(publish: P)
}