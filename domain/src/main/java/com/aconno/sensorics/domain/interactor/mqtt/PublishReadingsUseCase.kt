package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Reading

class PublishReadingsUseCase(
    private val listPublisher: List<Publisher>
) {

    fun execute(parameter: List<Reading>): List<Publisher> {
        val publishedPublishers = mutableListOf<Publisher>()

        listPublisher
            .forEach {

                if (parameter.isNotEmpty() && it.isPublishable(parameter[0].device)) {
                    it.publish(parameter)
                    publishedPublishers.add(it)
                }
            }

        return publishedPublishers
    }
}