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

        listPublisher
            .forEach {

                if (parameter.isNotEmpty() && it.isPublishable(parameter[0].device)) {
                    it.publish(parameter)
                    publishedPublishers.add(it)
                }
            }

        return Flowable.fromArray(publishedPublishers)
    }
}