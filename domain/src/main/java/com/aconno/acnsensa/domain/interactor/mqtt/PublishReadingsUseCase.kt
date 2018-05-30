package com.aconno.acnsensa.domain.interactor.mqtt

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.FlowableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Flowable

/**
 * @aconno
 */
class PublishReadingsUseCase(
    private val publisher: List<Publisher>
) : FlowableUseCaseWithParameter<List<Publisher>, List<Reading>> {

    override fun execute(parameter: List<Reading>): Flowable<List<Publisher>> {
        //TODO Need to check other Publishers other than Google Cloud

        val publishedPublishers = mutableListOf<Publisher>()
        publisher
            .filter { publisher -> publisher.isPublishable() }
            .forEach({
                publishedPublishers.add(it)

                parameter.forEach { at ->
                    it.publish(at)
                }
            })

        return Flowable.fromArray(publishedPublishers)
    }
}