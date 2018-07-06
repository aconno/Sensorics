package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Maybe

interface MaybeUseCase<T> {

    fun execute(): Maybe<T>
}