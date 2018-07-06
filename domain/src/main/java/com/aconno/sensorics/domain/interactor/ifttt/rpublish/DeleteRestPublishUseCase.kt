package com.aconno.sensorics.domain.interactor.ifttt.rpublish

import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRestPublishUseCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<RESTPublish> {
    override fun execute(parameter: RESTPublish): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTPublish(parameter)
        }
    }
}