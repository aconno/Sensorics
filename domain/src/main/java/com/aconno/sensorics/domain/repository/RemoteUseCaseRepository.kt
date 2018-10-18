package com.aconno.sensorics.domain.repository

import io.reactivex.Completable

interface RemoteUseCaseRepository {
    fun updateUseCases(): Completable
}