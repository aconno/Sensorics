package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Completable

interface CompletableUseCase {

    fun execute(): Completable
}