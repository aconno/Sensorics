package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.interactor.type.CompletableUseCase
import io.reactivex.Completable

class CloseConnectionUseCase(
    private val publisher: List<Publisher>
) : CompletableUseCase {

    override fun execute(): Completable {
        publisher.forEach { it.closeConnection() }
        return Completable.complete()
    }
}