package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Completable

/**
 * @aconno
 */
class SyncReadingUseCase(
    private val publisher: Publisher
) : CompletableUseCaseWithParameter<Reading> {

    override fun execute(parameter: Reading): Completable {
        publisher.publish(parameter)
        return Completable.complete()
    }
}