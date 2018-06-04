package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Maybe

interface MaybeUseCaseWithTwoParameters<T, in P1, in P2> {

    fun execute(firstParameter: P1, secondParameter: P2): Maybe<T>
}