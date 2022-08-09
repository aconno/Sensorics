package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Reading

class PublishReadingsUseCase(
    private val listPublisher: List<Publisher<*>>
) { // TODO: MAKE THIS A PROPER USE CASE AND MAYBE DON'T RETURN ANYTHING
    fun execute(parameter: List<Reading>): List<Publisher<*>> {
        return if (parameter.isNotEmpty()) {
            listPublisher.forEach {
                it.publish(parameter)
            }
            listPublisher
        } else {
            listOf()
        }
    }
}