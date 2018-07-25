package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.interactor.type.FlowableUseCaseWithParameter
import io.reactivex.Flowable

class PublishReadingsUseCase(
    private val listPublisher: List<Publisher>
) : FlowableUseCaseWithParameter<List<Publisher>, List<Reading>> {

    override fun execute(parameter: List<Reading>): Flowable<List<Publisher>> {
        val publishedPublishers = mutableListOf<Publisher>()

        var isPublished = false

        listPublisher
            .forEach {
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
            }

        return Flowable.fromArray(publishedPublishers)
    }
}