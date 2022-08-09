package com.aconno.sensorics.data.repository

import io.reactivex.Maybe
import io.reactivex.Single

interface PublishDao<E> {
    val all: Single<List<E>>
    val allEnabled: Single<List<E>>
    fun getById(id: Long): Maybe<E>
    fun insert(entity: E): Long
    fun update(entity: E)
    fun delete(entity: E)
}