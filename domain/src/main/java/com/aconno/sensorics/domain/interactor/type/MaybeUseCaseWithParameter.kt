package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Maybe

/**
 * @author aconno
 */
interface MaybeUseCaseWithParameter<T, in P> {
    fun execute(parameter: P): Maybe<T>
}