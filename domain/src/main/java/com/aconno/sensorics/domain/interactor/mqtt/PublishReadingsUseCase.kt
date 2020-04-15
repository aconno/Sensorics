package com.aconno.sensorics.domain.interactor.mqtt

import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithTwoParameters
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Completable

class PublishReadingsUseCase :
    CompletableUseCaseWithTwoParameters<List<Publisher<*>>, List<Reading>> {
    override fun execute(parameter1: List<Publisher<*>>, parameter2: List<Reading>): Completable {
        return Completable.fromAction {
            parameter1.forEach {
                it.publish(parameter2)
            }
        }
    }
}