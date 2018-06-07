package com.aconno.acnsensa.domain.interactor.mqtt

import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.type.FlowableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorReading
import io.reactivex.Flowable

/**
 * @aconno
 */
class PublishReadingsUseCase(
    private val listPublisher: List<Publisher>
) : FlowableUseCaseWithParameter<List<Publisher>, List<SensorReading>> {

    override fun execute(parameter: List<SensorReading>): Flowable<List<Publisher>> {
        val publishedPublishers = mutableListOf<Publisher>()

        var isPublished = false

        listPublisher
            .forEach({
                parameter
                    .filter { ad -> it.isPublishable(ad.device) }
                    .forEach { at ->
                        it.publish(at)
                        isPublished = true
                    }

                if (isPublished) {
                    publishedPublishers.add(it)
                    isPublished = false
                }
            })

        return Flowable.fromArray(publishedPublishers)
    }
}