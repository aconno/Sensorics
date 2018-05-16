package com.aconno.acnsensa.domain.interactor.mqtt

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Completable

/**
 * @aconno
 */
class PublishReadingsUseCase(
    private val publisher: Publisher
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            publisher.publish(reading)
        }
        return Completable.complete()
    }
}