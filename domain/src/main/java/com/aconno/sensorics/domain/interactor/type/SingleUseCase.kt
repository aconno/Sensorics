package com.aconno.sensorics.domain.interactor.type

import io.reactivex.Single

interface SingleUseCase<T> {

    fun execute(): Single<T>
}