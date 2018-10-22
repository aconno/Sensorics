package com.aconno.sensorics.domain.repository

import io.reactivex.Maybe

interface RemoteUseCaseRepository {
    fun updateUseCases(sensorName: String): Maybe<String>
}