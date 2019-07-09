package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Completable

interface CompletableUseCaseWithTwoParameters<in P1, in P2> {

    fun execute(parameter1: P1, parameter2: P2): Completable
}