package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Single

interface SingleUseCaseWithTwoParameters<T, in P1, in P2> {
    fun execute(parameter1: P1, parameter2 : P2): Single<T>
}