package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Completable

interface CompletableUseCase {

    fun execute(): Completable
}