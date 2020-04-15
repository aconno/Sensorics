package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class CloseConnectionUseCase : CompletableUseCaseWithParameter<List<Publisher<*>>> {

    override fun execute(parameter: List<Publisher<*>>): Completable {
        parameter.forEach { it.closeConnection() }
        return Completable.complete()
    }
}