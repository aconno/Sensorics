package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Observable

interface ObservableUseCaseWithParameter<T, in P> {

    fun execute(parameter: P): Observable<T>
}