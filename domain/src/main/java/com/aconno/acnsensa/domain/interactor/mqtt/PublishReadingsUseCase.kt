package com.aconno.acnsensa.domain.interactor.mqtt

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Completable

/**
 * @aconno
 */
class PublishReadingsUseCase(
    private val publisher: List<Publisher>
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        //TODO Need to check other Publishers other than Google Cloud
        publisher.forEach({
            parameter.forEach { at -> it.publish(at) }
        })

        return Completable.complete()
    }
}