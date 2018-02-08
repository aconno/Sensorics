package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Completable

interface CompletableUseCaseWithTwoParameters<in P1, in P2> {

    fun execute(firstParameter: P1, secondParameter: P2): Completable
}