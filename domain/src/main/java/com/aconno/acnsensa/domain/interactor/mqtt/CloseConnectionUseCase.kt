package com.aconno.acnsensa.domain.interactor.mqtt

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCase
import io.reactivex.Completable

class CloseConnectionUseCase(
    private val publisher: List<Publisher>
) : CompletableUseCase {

    override fun execute(): Completable {
        publisher.forEach { it.closeConnection() }
        return Completable.complete()
    }
}