package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Observable

interface ObservableUseCaseWithTwoParameters<T, in P1, in P2> {

    fun execute(firstParameter: P1, secondParameter: P2): Observable<T>
}